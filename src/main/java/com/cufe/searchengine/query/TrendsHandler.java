package com.cufe.searchengine.query;

import com.cufe.searchengine.db.table.PersonsTable;
import com.cufe.searchengine.util.NLPUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class TrendsHandler {
	private static final Logger log = LoggerFactory.getLogger(TrendsHandler.class);

    @Autowired
	private PersonsTable personsTable;

	public void updateTrends(String query, String countryCode) throws Exception {
        List<String> personNames = NLPUtils.performNER(query);
        for (String name : personNames) {
            personsTable.updatePersonCount(name, countryCode);
        }
    }

    /**
	 * @return list of top 10 trending persons names
	 */
    public List<String> getTrends(String countryCode) throws Exception {
        return personsTable.getTopPersons(countryCode);
    }
}
