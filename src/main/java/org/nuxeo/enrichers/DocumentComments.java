package org.nuxeo.enrichers;

import static org.nuxeo.ecm.core.io.registry.reflect.Instantiations.SINGLETON;
import static org.nuxeo.ecm.core.io.registry.reflect.Priorities.REFERENCE;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonGenerator;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.io.marshallers.json.enrichers.AbstractJsonEnricher;
import org.nuxeo.ecm.core.io.registry.reflect.Setup;
import org.nuxeo.ecm.core.schema.utils.DateParser;
import org.nuxeo.ecm.platform.comment.api.CommentableDocument;

@Setup(mode = SINGLETON, priority = REFERENCE)
public class DocumentComments extends AbstractJsonEnricher<DocumentModel> {

	public static final String NAME = "documentComments";

	public DocumentComments() {
		super(NAME);
	}

	@Override
	public void write(JsonGenerator jg, DocumentModel doc) throws IOException {
		jg.writeFieldName(NAME);
		CommentableDocument adapter = doc.getAdapter(CommentableDocument.class);
		printComments(jg, adapter, null);
		jg.flush();
	}

	private void printComments(JsonGenerator jg, CommentableDocument adapter, DocumentModel commentDoc)
			throws IOException, JsonGenerationException {
		List<DocumentModel> comments = commentDoc == null ? adapter.getComments() : adapter.getComments(commentDoc);
		if (!CollectionUtils.isEmpty(comments)) {
			if (commentDoc != null) {
				jg.writeFieldName("replies");
			}
			jg.writeStartArray();
			for (DocumentModel comment : comments) {
				jg.writeStartObject();
				jg.writeStringField("text", (String) comment.getPropertyValue("comment:text"));
				jg.writeStringField("author", (String) comment.getPropertyValue("comment:author"));
				jg.writeStringField("creationDate", DateParser
						.formatW3CDateTime(((Calendar) comment.getPropertyValue("comment:creationDate")).getTime()));
				printComments(jg, adapter, comment);
				jg.writeEndObject();
			}
			jg.writeEndArray();
		}
	}

}
