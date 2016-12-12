package org.nuxeo.enrichers;

import static org.nuxeo.ecm.core.io.registry.reflect.Instantiations.SINGLETON;
import static org.nuxeo.ecm.core.io.registry.reflect.Priorities.REFERENCE;

import java.io.IOException;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.JsonGenerator;
import org.nuxeo.ecm.automation.core.scripting.DateWrapper;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.LifeCycleConstants;
import org.nuxeo.ecm.core.io.marshallers.json.enrichers.AbstractJsonEnricher;
import org.nuxeo.ecm.core.io.registry.reflect.Setup;
import org.nuxeo.ecm.core.query.sql.NXQL;
import org.nuxeo.ecm.core.query.sql.model.Operator;

@Setup(mode = SINGLETON, priority = REFERENCE)
public class LatestNewsDocumentEnricher extends AbstractJsonEnricher<DocumentModel> {

	protected static final Log log = LogFactory.getLog(LatestNewsDocumentEnricher.class);

	public static final String NAME = "getLatestNews";

	public LatestNewsDocumentEnricher(String name) {
		super(name);
	}

	@Override
	public void write(JsonGenerator jg, DocumentModel doc) throws IOException {
		jg.writeFieldName(NAME);
		writeEntity(getNewsDocuments(doc.getCoreSession(), doc.getId()), jg);
	}

	protected List<DocumentModel> getNewsDocuments(CoreSession session, String parentId) {
		StringBuilder query = new StringBuilder("SELECT * FROM Document WHERE ");
		query.append(NXQL.ECM_PARENTID).append(Operator.EQ.toString()).append(NXQL.escapeString(parentId))
				.append(" " + Operator.AND.toString() + " ").append("nhstrust:articledate").append(Operator.GTEQ.toString())
				.append(new DateWrapper().days(-30).toString()).append(" " + Operator.AND.toString() + " ")
				.append(NXQL.ECM_LIFECYCLESTATE).append(Operator.NOTEQ.toString())
				.append(NXQL.escapeString(LifeCycleConstants.DELETED_STATE)).append(" " + Operator.AND.toString() + " ")
				.append(NXQL.ECM_ISCHECKEDIN).append(Operator.EQ.toString() + "0");
		List<DocumentModel> docs = session.query(query.toString());
		if (log.isDebugEnabled()) {
			log.debug("<getChildren> query: " + query.toString() + ", " + docs);
		}
		return docs;
	}

}
