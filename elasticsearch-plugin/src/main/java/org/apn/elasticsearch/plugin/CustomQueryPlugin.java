/**
 * 
 */
package org.apn.elasticsearch.plugin;

import org.elasticsearch.plugins.Plugin;
import org.elasticsearch.plugins.SearchPlugin;

import java.util.List;

import static java.util.Collections.singletonList;

/**
 * @author amit.nema
 *
 */
public class CustomQueryPlugin extends Plugin implements SearchPlugin {

	@Override
	public List<QuerySpec<?>> getQueries() {
        return singletonList(new QuerySpec<>(CustomQueryBuilder.NAME, CustomQueryBuilder::new, CustomQueryBuilder::fromXContent));
	}
}
