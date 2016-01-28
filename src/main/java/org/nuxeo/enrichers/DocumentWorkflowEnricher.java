package org.nuxeo.enrichers;

import static org.nuxeo.ecm.core.io.registry.reflect.Instantiations.SINGLETON;
import static org.nuxeo.ecm.core.io.registry.reflect.Priorities.REFERENCE;

import java.io.IOException;
import java.util.List;

import org.codehaus.jackson.JsonGenerator;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.io.marshallers.json.enrichers.AbstractJsonEnricher;
import org.nuxeo.ecm.core.io.registry.reflect.Setup;
import org.nuxeo.ecm.platform.routing.api.DocumentRoute;
import org.nuxeo.ecm.platform.routing.api.DocumentRoutingService;
import org.nuxeo.runtime.api.Framework;

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
			jg.writeObjectField("attachedDocumentsIds", documentRoute.getAttachedDocuments());
			jg.writeObjectField("id", documentRoute.getDocument().getId());
			jg.writeStringField("initiator", documentRoute.getInitiator());
			jg.writeStringField("name", documentRoute.getName());
			jg.writeStringField("title",documentRoute.getDocument().getTitle());
			jg.writeStringField("state",documentRoute.getDocument().getCurrentLifeCycleState());
			jg.writeEndObject();
		}
		jg.writeEndArray();
		jg.flush();
	}

}
