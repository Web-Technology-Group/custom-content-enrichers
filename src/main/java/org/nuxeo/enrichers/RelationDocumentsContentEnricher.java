package org.nuxeo.enrichers;

import static org.nuxeo.ecm.core.io.registry.reflect.Instantiations.SINGLETON;
import static org.nuxeo.ecm.core.io.registry.reflect.Priorities.REFERENCE;

import java.io.Closeable;
import java.io.IOException;
import java.security.Principal;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.JsonGenerator;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.core.api.UnrestrictedSessionRunner;
import org.nuxeo.ecm.core.api.impl.DocumentModelListImpl;
import org.nuxeo.ecm.core.api.security.SecurityConstants;
import org.nuxeo.ecm.core.io.marshallers.json.enrichers.AbstractJsonEnricher;
import org.nuxeo.ecm.core.io.registry.context.MaxDepthReachedException;
import org.nuxeo.ecm.core.io.registry.context.RenderingContext.SessionWrapper;
import org.nuxeo.ecm.core.io.registry.reflect.Setup;
import org.nuxeo.ecm.core.query.sql.NXQL;
import org.nuxeo.ecm.core.query.sql.model.Operator;

@Setup(mode = SINGLETON, priority = REFERENCE)
public class RelationDocumentsContentEnricher extends AbstractJsonEnricher<DocumentModel> {

	private static final Log LOG = LogFactory.getLog(RelationDocumentsContentEnricher.class);

	public static final String NAME = "relationDocuments";

	public RelationDocumentsContentEnricher() {
		super(NAME);
	}

	@Override
	public void write(JsonGenerator jg, DocumentModel document) throws IOException {
		Principal principal = ctx.getSession(document).getSession().getPrincipal();
		try (SessionWrapper wrapper = ctx.getSession(document)) {
			String id = document.getId();
			new UnrestrictedSessionRunner(wrapper.getSession()) {
				@Override
				public void run() {
					// control the graph depth
					try (Closeable resource = ctx.wrap().controlDepth().open()) {
						StringBuilder query = new StringBuilder("SELECT * FROM ");
						query.append("DefaultRelation").append(" WHERE ").append("relation:source").append("=")
								.append(NXQL.escapeString(id)).append(Operator.OR.toString())
								.append(" " + "relation:target" + " ").append("=").append(NXQL.escapeString(id));
						DocumentModelList relations = session.query(query.toString());
						Predicate<DocumentModel> canReadSourceDocument = doc -> session.hasPermission(principal,
								new IdRef((String) doc.getPropertyValue("relation:source")), SecurityConstants.READ);
						Predicate<DocumentModel> canReadTargetDocument = doc -> session.hasPermission(principal,
								new IdRef((String) doc.getPropertyValue("relation:target")), SecurityConstants.READ);
						Predicate<DocumentModel> canReadRelatedDocuments = canReadSourceDocument
								.and(canReadTargetDocument);
						DocumentModelList accessableRelations = relations.stream().filter(canReadRelatedDocuments)
								.collect(Collectors.toCollection(DocumentModelListImpl::new));
						jg.writeFieldName(NAME);
						// delegate the marshalling to Nuxeo Platform
						writeEntity(accessableRelations, jg);
					} catch (MaxDepthReachedException e) {
						// do not apply enricher
					} catch (IOException e) {
						LOG.error(e, e);
					}

				}
			}.runUnrestricted();
		}

	}
}
