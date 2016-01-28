package org.nuxeo.enrichers;

import static org.nuxeo.ecm.core.io.registry.reflect.Instantiations.SINGLETON;
import static org.nuxeo.ecm.core.io.registry.reflect.Priorities.REFERENCE;

import java.io.IOException;
import java.util.List;

import org.codehaus.jackson.JsonGenerator;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.io.marshallers.json.enrichers.AbstractJsonEnricher;
import org.nuxeo.ecm.core.io.registry.reflect.Setup;
import org.nuxeo.ecm.platform.tag.Tag;
import org.nuxeo.ecm.platform.tag.TagService;
import org.nuxeo.runtime.api.Framework;

@Setup(mode = SINGLETON, priority = REFERENCE)
public class TagsContentEnricher extends AbstractJsonEnricher<DocumentModel> {

	public static final String NAME = "getDocumentTags";

	public TagsContentEnricher() {
		super(NAME);
	}

	@Override
	public void write(JsonGenerator jg, DocumentModel doc) throws IOException {
		TagService ts = Framework.getService(TagService.class);
		List<Tag> tags = ts.getDocumentTags(doc.getCoreSession(), doc.getId(), null);
		jg.writeFieldName(NAME);
		jg.writeStartArray();
		for (Tag tag : tags) {
			jg.writeStartObject();
			jg.writeStringField("label", tag.getLabel());
			jg.writeStringField("weight", Long.toString(tag.getWeight()));
			jg.writeEndObject();
		}
		jg.writeEndArray();
		jg.flush();
	}

}
