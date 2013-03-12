/**
 * Copyright (C) 2012 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.master.exchange.impl;

import java.util.ArrayList;
import java.util.List;

import org.joda.beans.Bean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opengamma.id.UniqueId;
import com.opengamma.master.cache.AbstractEHCachingMaster;
import com.opengamma.master.cache.EHCachingPagedSearchCache;
import com.opengamma.master.exchange.ExchangeDocument;
import com.opengamma.master.exchange.ExchangeHistoryRequest;
import com.opengamma.master.exchange.ExchangeHistoryResult;
import com.opengamma.master.exchange.ExchangeMaster;
import com.opengamma.master.exchange.ExchangeSearchRequest;
import com.opengamma.master.exchange.ExchangeSearchResult;
import com.opengamma.master.exchange.ExchangeSearchSortOrder;
import com.opengamma.util.paging.Paging;
import com.opengamma.util.paging.PagingRequest;
import com.opengamma.util.tuple.ObjectsPair;

import net.sf.ehcache.CacheManager;

/**
 * A cache decorating a {@code ExchangeMaster}, mainly intended to reduce the frequency and repetition of queries
 * from the management UI to a {@code DbExchangeMaster}. In particular, prefetching is employed in paged queries,
 * which tend to scale poorly.
 * <p>
 * The cache is implemented using {@code EHCache}.
 */
public class EHCachingExchangeMaster extends AbstractEHCachingMaster<ExchangeDocument> implements ExchangeMaster {

  /** Logger. */
  private static final Logger s_logger = LoggerFactory.getLogger(EHCachingExchangeMaster.class);

  /** The document search cache */
  private EHCachingPagedSearchCache _documentSearchCache;
  
  /** The history search cache */
  private EHCachingPagedSearchCache _historySearchCache;
  
  /**
   * Creates an instance over an underlying master specifying the cache manager.
   *
   * @param name          the cache name, not empty
   * @param underlying    the underlying Exchange source, not null
   * @param cacheManager  the cache manager, not null
   */
  public EHCachingExchangeMaster(final String name, final ExchangeMaster underlying, final CacheManager cacheManager) {
    super(name, underlying, cacheManager);
    
    // Create the doc search cache and register a exchange master searcher
    _documentSearchCache = new EHCachingPagedSearchCache(name + "Document", cacheManager, new EHCachingPagedSearchCache.Searcher() {
      @Override
      public ObjectsPair<Integer, List<UniqueId>> search(Bean request, PagingRequest pagingRequest) {
        // Fetch search results from underlying master
        ExchangeSearchResult result = ((ExchangeMaster) getUnderlying()).search((ExchangeSearchRequest)
            EHCachingPagedSearchCache.withPagingRequest(request, pagingRequest));

        // Cache the result documents
        EHCachingPagedSearchCache.cacheDocuments(result.getDocuments(), getUidToDocumentCache());

        // Return the list of result UniqueIds
        return new ObjectsPair<>(result.getPaging().getTotalItems(),
                                 EHCachingPagedSearchCache.extractUniqueIds(result.getDocuments()));
      }
    });

    // Create the history search cache and register a security master searcher
    _historySearchCache = new EHCachingPagedSearchCache(name + "History", cacheManager, new EHCachingPagedSearchCache.Searcher() {
      @Override
      public ObjectsPair<Integer, List<UniqueId>> search(Bean request, PagingRequest pagingRequest) {
        // Fetch search results from underlying master
        ExchangeHistoryResult result = ((ExchangeMaster) getUnderlying()).history((ExchangeHistoryRequest)
            EHCachingPagedSearchCache.withPagingRequest(request, pagingRequest));

        // Cache the result documents
        EHCachingPagedSearchCache.cacheDocuments(result.getDocuments(), getUidToDocumentCache());

        // Return the list of result UniqueIds
        return new ObjectsPair<>(result.getPaging().getTotalItems(),
                                 EHCachingPagedSearchCache.extractUniqueIds(result.getDocuments()));
      }
    });
    
    // Prime search cache
    ExchangeSearchRequest defaultSearch = new ExchangeSearchRequest();
    defaultSearch.setSortOrder(ExchangeSearchSortOrder.NAME_ASC);
    _documentSearchCache.prefetch(defaultSearch, PagingRequest.FIRST_PAGE);
    
  }

  @Override
  public ExchangeSearchResult search(ExchangeSearchRequest request) {
    // Ensure that the relevant prefetch range is cached, otherwise fetch and cache any missing sub-ranges in background
    _documentSearchCache.prefetch(EHCachingPagedSearchCache.withPagingRequest(request, null), request.getPagingRequest());

    // Fetch the paged request range; if not entirely cached then fetch and cache it in foreground
    ObjectsPair<Integer, List<UniqueId>> pair = _documentSearchCache.search(
        EHCachingPagedSearchCache.withPagingRequest(request, null),
        request.getPagingRequest(), false); // don't block until cached

    List<ExchangeDocument> documents = new ArrayList<>();
    for (UniqueId uniqueId : pair.getSecond()) {
      documents.add(get(uniqueId));
    }

    ExchangeSearchResult result = new ExchangeSearchResult(documents);
    result.setPaging(Paging.of(request.getPagingRequest(), pair.getFirst()));

    // Debug: check result against underlying
    if (EHCachingPagedSearchCache.TEST_AGAINST_UNDERLYING) {
      ExchangeSearchResult check = ((ExchangeMaster) getUnderlying()).search(request);
      if (!result.getPaging().equals(check.getPaging())) {
        s_logger.error("_documentSearchCache.getCache().getName() + \" returned paging:\\n\"" + result.getPaging() +
                           "\nbut the underlying master returned paging:\n" + check.getPaging());
      }
      if (!result.getDocuments().equals(check.getDocuments())) {
        s_logger.error(_documentSearchCache.getCache().getName() + " returned documents:\n" + result.getDocuments() +
                           "\nbut the underlying master returned documents:\n" + check.getDocuments());
      }
    }

    return result;
  }

  @Override
  public ExchangeHistoryResult history(ExchangeHistoryRequest request) {

    // Ensure that the relevant prefetch range is cached, otherwise fetch and cache any missing sub-ranges in background
    _historySearchCache.prefetch(EHCachingPagedSearchCache.withPagingRequest(request, null), request.getPagingRequest());

    // Fetch the paged request range; if not entirely cached then fetch and cache it in foreground
    ObjectsPair<Integer, List<UniqueId>> pair = _historySearchCache.search(
        EHCachingPagedSearchCache.withPagingRequest(request, null),
        request.getPagingRequest(), false); // don't block until cached

    List<ExchangeDocument> documents = new ArrayList<>();
    for (UniqueId uniqueId : pair.getSecond()) {
      documents.add(get(uniqueId));
    }

    ExchangeHistoryResult result = new ExchangeHistoryResult(documents);
    result.setPaging(Paging.of(request.getPagingRequest(), pair.getFirst()));
    return result;    
  }

}
