/**
 * Copyright (C) 2009 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.master;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.joda.beans.BeanBuilder;
import org.joda.beans.BeanDefinition;
import org.joda.beans.JodaBeanUtils;
import org.joda.beans.MetaProperty;
import org.joda.beans.Property;
import org.joda.beans.PropertyDefinition;
import org.joda.beans.impl.direct.DirectBean;
import org.joda.beans.impl.direct.DirectMetaBean;
import org.joda.beans.impl.direct.DirectMetaProperty;
import org.joda.beans.impl.direct.DirectMetaPropertyMap;

import com.opengamma.util.PublicSPI;
import com.opengamma.util.paging.Paging;

/**
 * Result providing a list of documents with paging.
 * 
 * @param <D>  the type of the document
 */
@PublicSPI
@BeanDefinition
public abstract class AbstractDocumentsResult<D extends AbstractDocument> extends DirectBean {

  /**
   * The paging information, not null if correctly created.
   */
  @PropertyDefinition
  private Paging _paging;
  /**
   * The documents, not null.
   */
  @PropertyDefinition
  private final List<D> _documents = new ArrayList<D>();

  /**
   * Creates an instance.
   */
  public AbstractDocumentsResult() {
  }

  /**
   * Creates an instance.
   * @param coll  the collection of documents to add, not null
   */
  public AbstractDocumentsResult(Collection<D> coll) {
    _documents.addAll(coll);
    _paging = Paging.ofAll(coll);
  }

  //-------------------------------------------------------------------------
  /**
   * Gets the first document, or null if no documents.
   * @return the first document, null if none
   */
  public D getFirstDocument() {
    return getDocuments().size() > 0 ? getDocuments().get(0) : null;
  }

  //------------------------- AUTOGENERATED START -------------------------
  ///CLOVER:OFF
  /**
   * The meta-bean for {@code AbstractDocumentsResult}.
   * @return the meta-bean, not null
   */
  @SuppressWarnings("rawtypes")
  public static AbstractDocumentsResult.Meta meta() {
    return AbstractDocumentsResult.Meta.INSTANCE;
  }
  /**
   * The meta-bean for {@code AbstractDocumentsResult}.
   * @param <R>  the bean's generic type
   * @param cls  the bean's generic type
   * @return the meta-bean, not null
   */
  @SuppressWarnings("unchecked")
  public static <R extends AbstractDocument> AbstractDocumentsResult.Meta<R> metaAbstractDocumentsResult(Class<R> cls) {
    return AbstractDocumentsResult.Meta.INSTANCE;
  }
  static {
    JodaBeanUtils.registerMetaBean(AbstractDocumentsResult.Meta.INSTANCE);
  }

  @SuppressWarnings("unchecked")
  @Override
  public AbstractDocumentsResult.Meta<D> metaBean() {
    return AbstractDocumentsResult.Meta.INSTANCE;
  }

  @Override
  protected Object propertyGet(String propertyName, boolean quiet) {
    switch (propertyName.hashCode()) {
      case -995747956:  // paging
        return getPaging();
      case 943542968:  // documents
        return getDocuments();
    }
    return super.propertyGet(propertyName, quiet);
  }

  @SuppressWarnings("unchecked")
  @Override
  protected void propertySet(String propertyName, Object newValue, boolean quiet) {
    switch (propertyName.hashCode()) {
      case -995747956:  // paging
        setPaging((Paging) newValue);
        return;
      case 943542968:  // documents
        setDocuments((List<D>) newValue);
        return;
    }
    super.propertySet(propertyName, newValue, quiet);
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj != null && obj.getClass() == this.getClass()) {
      AbstractDocumentsResult<?> other = (AbstractDocumentsResult<?>) obj;
      return JodaBeanUtils.equal(getPaging(), other.getPaging()) &&
          JodaBeanUtils.equal(getDocuments(), other.getDocuments());
    }
    return false;
  }

  @Override
  public int hashCode() {
    int hash = getClass().hashCode();
    hash += hash * 31 + JodaBeanUtils.hashCode(getPaging());
    hash += hash * 31 + JodaBeanUtils.hashCode(getDocuments());
    return hash;
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the paging information, not null if correctly created.
   * @return the value of the property
   */
  public Paging getPaging() {
    return _paging;
  }

  /**
   * Sets the paging information, not null if correctly created.
   * @param paging  the new value of the property
   */
  public void setPaging(Paging paging) {
    this._paging = paging;
  }

  /**
   * Gets the the {@code paging} property.
   * @return the property, not null
   */
  public final Property<Paging> paging() {
    return metaBean().paging().createProperty(this);
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the documents, not null.
   * @return the value of the property
   */
  public List<D> getDocuments() {
    return _documents;
  }

  /**
   * Sets the documents, not null.
   * @param documents  the new value of the property
   */
  public void setDocuments(List<D> documents) {
    this._documents.clear();
    this._documents.addAll(documents);
  }

  /**
   * Gets the the {@code documents} property.
   * @return the property, not null
   */
  public final Property<List<D>> documents() {
    return metaBean().documents().createProperty(this);
  }

  //-----------------------------------------------------------------------
  /**
   * The meta-bean for {@code AbstractDocumentsResult}.
   */
  public static class Meta<D extends AbstractDocument> extends DirectMetaBean {
    /**
     * The singleton instance of the meta-bean.
     */
    @SuppressWarnings("rawtypes")
    static final Meta INSTANCE = new Meta();

    /**
     * The meta-property for the {@code paging} property.
     */
    private final MetaProperty<Paging> _paging = DirectMetaProperty.ofReadWrite(
        this, "paging", AbstractDocumentsResult.class, Paging.class);
    /**
     * The meta-property for the {@code documents} property.
     */
    @SuppressWarnings({"unchecked", "rawtypes" })
    private final MetaProperty<List<D>> _documents = DirectMetaProperty.ofReadWrite(
        this, "documents", AbstractDocumentsResult.class, (Class) List.class);
    /**
     * The meta-properties.
     */
    private final Map<String, MetaProperty<?>> _metaPropertyMap$ = new DirectMetaPropertyMap(
        this, null,
        "paging",
        "documents");

    /**
     * Restricted constructor.
     */
    protected Meta() {
    }

    @Override
    protected MetaProperty<?> metaPropertyGet(String propertyName) {
      switch (propertyName.hashCode()) {
        case -995747956:  // paging
          return _paging;
        case 943542968:  // documents
          return _documents;
      }
      return super.metaPropertyGet(propertyName);
    }

    @Override
    public BeanBuilder<? extends AbstractDocumentsResult<D>> builder() {
      throw new UnsupportedOperationException("AbstractDocumentsResult is an abstract class");
    }

    @SuppressWarnings({"unchecked", "rawtypes" })
    @Override
    public Class<? extends AbstractDocumentsResult<D>> beanType() {
      return (Class) AbstractDocumentsResult.class;
    }

    @Override
    public Map<String, MetaProperty<?>> metaPropertyMap() {
      return _metaPropertyMap$;
    }

    //-----------------------------------------------------------------------
    /**
     * The meta-property for the {@code paging} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<Paging> paging() {
      return _paging;
    }

    /**
     * The meta-property for the {@code documents} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<List<D>> documents() {
      return _documents;
    }

  }

  ///CLOVER:ON
  //-------------------------- AUTOGENERATED END --------------------------
}
