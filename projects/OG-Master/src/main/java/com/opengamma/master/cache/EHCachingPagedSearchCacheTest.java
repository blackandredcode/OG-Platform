/**
 * Copyright (C) 2012 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.master.cache;

import static org.testng.AssertJUnit.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.joda.beans.Bean;
import org.testng.annotations.Test;

import com.opengamma.id.UniqueId;
import com.opengamma.master.security.SecuritySearchRequest;
import com.opengamma.util.paging.PagingRequest;
import com.opengamma.util.tuple.ObjectsPair;

import net.sf.ehcache.CacheManager;

@Test
public class EHCachingPagedSearchCacheTest {

  private static final int TOTAL_SIZE = 1000;
  private static final String TEST_SCHEME = "TEST";

  @Test
  public void testSearchCache() {
    for (int requestSize = 1; requestSize < TOTAL_SIZE; requestSize = requestSize + 17) {
      for (int requestStartStepSize = 1; requestStartStepSize < TOTAL_SIZE / 2; requestStartStepSize = requestStartStepSize + 71) {
        EHCachingPagedSearchCache searchCache = getCleanSearchCache();
        for (int requestStartPos = 0; requestStartPos * requestStartStepSize < TOTAL_SIZE * 4; requestStartPos++) {
          PagingRequest pagingRequest = PagingRequest.ofIndex((requestStartPos * requestStartStepSize) % TOTAL_SIZE, requestSize);
          System.out.println(pagingRequest);
          assertEquals(searchCache.search(new SecuritySearchRequest(), pagingRequest, false).getSecond(),
                       buildResultIDs(
                         PagingRequest.ofIndex(
                           pagingRequest.getFirstItem(),
                           Math.min(pagingRequest.getLastItem() - pagingRequest.getFirstItem(),
                                    TOTAL_SIZE - pagingRequest.getFirstItem()
                           )
                         )
                       )
          );
        }
      }
    }
  }

  /**
   * Returns an empty cache manager
   * @return the cache manager
   */
  private CacheManager getCleanCacheManager() {
    CacheManager cacheManager = CacheManager.getInstance();
    cacheManager.clearAll();
    cacheManager.removalAll();
    return cacheManager;
  }

  private EHCachingPagedSearchCache getCleanSearchCache() {
    return new EHCachingPagedSearchCache("Test", getCleanCacheManager(), new EHCachingPagedSearchCache.Searcher() {
      @Override
      public ObjectsPair<Integer, List<UniqueId>> search(Bean request, PagingRequest pagingRequest) {

        List<UniqueId> result = buildResultIDs(pagingRequest);

        return new ObjectsPair<>(TOTAL_SIZE, result);
      }
    });
  }

  private List<UniqueId> buildResultIDs(PagingRequest pagingRequest) {
    List<UniqueId> result = new ArrayList<>();

    for (int i = pagingRequest.getFirstItem(); i < pagingRequest.getLastItem(); i++) {
      UniqueId uniqueId = UniqueId.of(TEST_SCHEME, Integer.toString(i), "1");
      result.add(uniqueId);
    }
    return result;
  }

}
