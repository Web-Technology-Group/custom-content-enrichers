package org.nuxeo.enrichers;

import static org.nuxeo.ecm.core.io.registry.reflect.Instantiations.SINGLETON;
import static org.nuxeo.ecm.core.io.registry.reflect.Priorities.REFERENCE;

import java.io.IOException;
import java.util.List;
import java.io.Serializable;
import java.util.Map.Entry;
import javax.servlet.http.HttpServletRequest;

import org.codehaus.jackson.JsonGenerator;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.platform.routing.api.DocumentRoute;
import org.nuxeo.ecm.platform.routing.api.DocumentRoutingService;
import org.nuxeo.ecm.core.io.marshallers.json.enrichers.AbstractJsonEnricher;
import org.nuxeo.ecm.core.io.registry.reflect.Setup;
import org.nuxeo.runtime.api.Framework;
import org.nuxeo.ecm.platform.routing.core.impl.GraphRoute;
import org.nuxeo.ecm.restapi.server.jaxrs.routing.io.util.JsonEncodeDecodeUtils;
import org.nuxeo.ecm.webengine.jaxrs.coreiodelegate.RenderingContextWebUtils;

@Setup(mode = SINGLETON, priority = REFERENCE)
public class DocumentWorkflowEnricher extends AbstractJsonEnricher<DocumentModel> {

	public static final String NAME = "allDocumentsWorkflow";

	public DocumentWorkflowEnricher() {
		super(NAME);
	}

	@Override
	public void write(JsonGenerator jg, DocumentModel doc) throws IOException {
		List<DocumentRoute> relatedRoutes = Framework.getService(DocumentRoutingService.class)
				.getDocumentRoutesForAttachedDocument(doc.getCoreSession(), doc.getId());

		jg.writeFieldName(NAME);
		jg.writeStartArray();
		for (DocumentRoute documentRoute : relatedRoutes) {
			jg.writeStartObject();
			jg.writeObjectField("id", documentRoute.getDocument().getId());
			jg.writeStringField("initiator", documentRoute.getInitiator());
			jg.writeStringField("name", documentRoute.getName());
			jg.writeStringField("title", documentRoute.getDocument().getTitle());
			jg.writeStringField("state", documentRoute.getDocument().getCurrentLifeCycleState());
			jg.writeStringField("workflowModelName", documentRoute.getModelName());
			for (String docId : documentRoute.getAttachedDocuments()) {
				jg.writeStringField("attachedDocumentIds", docId);
			}
			if (documentRoute instanceof GraphRoute) {
				GraphRoute graphRoute = (GraphRoute) documentRoute;
				jg.writeFieldName("variables");
				jg.writeStartObject();
				for (Entry<String, Serializable> e : graphRoute.getVariables().entrySet()) {
					JsonEncodeDecodeUtils.encodeVariableEntry(documentRoute.getDocument(),
							GraphRoute.PROP_VARIABLES_FACET, e, jg,
							ctx.getParameter(RenderingContextWebUtils.REQUEST_KEY));
				}
				jg.writeEndObject();
				String graphResourceUrl = "";
				if (documentRoute.isValidated()) {
					// it is a model
					graphResourceUrl = ctx.getBaseUrl() + "api/v1/workflowModel/"
							+ documentRoute.getDocument().getName() + "/graph";
				} else {
					// it is an instance
					graphResourceUrl = ctx.getBaseUrl() + "api/v1/workflow/" + documentRoute.getDocument().getId()
							+ "/graph";
				}
				jg.writeStringField("graphResource", graphResourceUrl);
			}
			jg.writeEndObject();
		}
		jg.writeEndArray();
		jg.flush();
	}

}
