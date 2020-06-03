package com.cufe.searchengine.util;

import org.springframework.core.io.Resource;
import org.springframework.beans.factory.annotation.Value;
import edu.stanford.nlp.pipeline.*;

import java.util.*;
import java.util.stream.Collectors;

public class NLPUtils {
	public static List<String> performNER(String query) {
    // set up pipeline properties
    Properties props = new Properties();
    props.setProperty("annotators", "tokenize,ssplit,pos,lemma,ner");

    // set up pipeline
    StanfordCoreNLP pipeline = new StanfordCoreNLP(props);

    // make an example document
    CoreDocument doc = new CoreDocument(query);

    // annotate the document
    pipeline.annotate(doc);
    
    // return person names
    List<String> personNames = new ArrayList<String>();
    for (CoreEntityMention em : doc.entityMentions()) {
        if (em.entityType().equals("PERSON")) {
            personNames.add(em.text());
        }
    }
    return personNames;
  }
}
