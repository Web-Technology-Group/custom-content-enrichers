package org.nuxeo.enrichers;

import static org.nuxeo.ecm.core.io.registry.reflect.Instantiations.SINGLETON;
import static org.nuxeo.ecm.core.io.registry.reflect.Priorities.REFERENCE;

import java.io.IOException;

import org.codehaus.jackson.JsonGenerator;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.io.marshallers.json.enrichers.AbstractJsonEnricher;
import org.nuxeo.ecm.core.io.registry.reflect.Setup;

@Setup(mode = SINGLETON, priority = REFERENCE)
public class PublishDocumentEnricher extends AbstractJsonEnricher<DocumentModel> { 
 
    public static final String NAME = "publishDocEnricher";
 
    public PublishDocumentEnricher() {
        super(NAME);
    }
 
    @Override
    public void write(JsonGenerator jg, DocumentModel doc) throws IOException {
      CoreSession session = doc.getCoreSession();
      DocumentModelList sessionList = session.getProxies(doc.getRef(), null);
      jg.writeFieldName(NAME);
      jg.writeStartArray();
      for(DocumentModel modelList : sessionList) {
    	  jg.writeStartObject();
    	  jg.writeStringField("path", modelList.getPathAsString());
          jg.writeStringField("id", modelList.getId());
          jg.writeStringField("name", modelList.getName());
          jg.writeStringField("title", modelList.getTitle());
          jg.writeStringField("type", modelList.getType());
          jg.writeEndObject();
      }
      jg.writeEndArray();
      jg.flush();
    }
 
}
