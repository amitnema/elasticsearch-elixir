/**
 * 
 */
package org.apn.elasticsearch.plugin;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;
import static org.elasticsearch.test.hamcrest.ElasticsearchAssertions.assertHitCount;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.ExecutionException;

import org.elasticsearch.cluster.metadata.IndexMetaData;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.plugins.Plugin;
import org.elasticsearch.test.ESIntegTestCase;
import org.junit.Before;
import org.junit.Test;

/**
 * @author amit.nema
 *
 */
public class CustomQueryPluginTest extends ESIntegTestCase {

	private static final String _INDEX = "books";
	private static final String _TYPE = "_doc";

	@Override
	protected Collection<Class<? extends Plugin>> nodePlugins() {
		return Arrays.asList(CustomQueryPlugin.class);
	}

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();
		final Settings.Builder settings = Settings.builder().put(IndexMetaData.SETTING_NUMBER_OF_REPLICAS, 1);
		prepareCreate(_INDEX)
				.setSettings(settings).addMapping(_TYPE, jsonBuilder().startObject().startObject("properties")
						.startObject("isbn").field("type", "text").endObject().endObject().endObject())
				.get().isAcknowledged();
		ensureGreen(TimeValue.timeValueSeconds(300));

		client().prepareIndex(_INDEX, _TYPE, "1")
				.setSource("isbn", "0393045218", "book_title", "The Mummies of Urumchi", "book_author",
						"E. J. W. Barber", "year_of_publication", "1999", "publisher", "W. W. Norton &amp; Company",
						"image_url_s", "http://images.amazon.com/images/P/0393045218.01.THUMBZZZ.jpg", "image_url_m",
						"http://images.amazon.com/images/P/0393045218.01.MZZZZZZZ.jpg", "image_url_l",
						"http://images.amazon.com/images/P/0393045218.01.LZZZZZZZ.jpg")
				.get();

		refresh();
	}

	@Test
	public void testCustomQuery() throws InterruptedException, ExecutionException {
		assertHitCount(client().prepareSearch(_INDEX).setQuery(new CustomQueryBuilder()).get(), 1L);
	}

}
