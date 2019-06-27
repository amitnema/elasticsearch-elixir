package org.apn.elasticsearch.plugin;

import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.Query;
import org.elasticsearch.common.io.stream.StreamInput;
import org.elasticsearch.common.io.stream.StreamOutput;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentParser;
import org.elasticsearch.index.query.AbstractQueryBuilder;
import org.elasticsearch.index.query.QueryShardContext;

import java.io.IOException;

/**
 * 
 * @author amit.nema
 *
 */
public class CustomQueryBuilder extends AbstractQueryBuilder<CustomQueryBuilder> {

	public static final String NAME = "custom";

	public CustomQueryBuilder() {
	}

	public CustomQueryBuilder(StreamInput in) throws IOException {
		super(in);
	}

	@Override
	protected void doWriteTo(StreamOutput out) throws IOException {
		// only the superclass has state
	}

	@Override
	protected void doXContent(XContentBuilder builder, Params params) throws IOException {
		builder.startObject(NAME).endObject();
	}

	public static CustomQueryBuilder fromXContent(XContentParser parser) throws IOException {
		XContentParser.Token token = parser.nextToken();
		assert token == XContentParser.Token.END_OBJECT;
		return new CustomQueryBuilder();
	}

	@Override
	protected Query doToQuery(QueryShardContext context) {
		return new MatchAllDocsQuery();
	}

	@Override
	protected int doHashCode() {
		return 0;
	}

	@Override
	protected boolean doEquals(CustomQueryBuilder other) {
		return true;
	}

	@Override
	public String getWriteableName() {
		return NAME;
	}
}
