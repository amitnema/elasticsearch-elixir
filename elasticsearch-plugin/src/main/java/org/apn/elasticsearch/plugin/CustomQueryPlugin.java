/**
 * 
 */
package org.apn.elasticsearch.plugin;

import static java.util.Collections.singletonList;

import java.util.List;

import org.elasticsearch.plugins.Plugin;
import org.elasticsearch.plugins.SearchPlugin;

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
