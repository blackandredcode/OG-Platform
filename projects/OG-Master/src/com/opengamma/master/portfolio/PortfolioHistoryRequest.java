/**
 * Copyright (C) 2009 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.master.portfolio;

import java.util.Map;

import javax.time.InstantProvider;

import org.joda.beans.BeanBuilder;
import org.joda.beans.BeanDefinition;
import org.joda.beans.JodaBeanUtils;
import org.joda.beans.MetaProperty;
import org.joda.beans.Property;
import org.joda.beans.PropertyDefinition;
import org.joda.beans.impl.direct.DirectBeanBuilder;
import org.joda.beans.impl.direct.DirectMetaProperty;
import org.joda.beans.impl.direct.DirectMetaPropertyMap;

import com.opengamma.id.ObjectIdentifiable;
import com.opengamma.master.AbstractHistoryRequest;
import com.opengamma.util.PublicSPI;

/**
 * Request for the history of a portfolio.
 * <p>
 * A full portfolio master implements historical storage of data.
 * History can be stored in two dimensions and this request provides searching.
 * <p>
 * The first historic dimension is the classic series of versions.
 * Each new version is stored in such a manor that previous versions can be accessed.
 * <p>
 * The second historic dimension is corrections.
 * A correction occurs when it is realized that the original data stored was incorrect.
 * A simple portfolio master might simply replace the original version with the corrected value.
 * A full implementation will store the correction in such a manner that it is still possible
 * to obtain the value before the correction was made.
 * <p>
 * For example, a portfolio added on Monday and updated on Thursday has two versions.
 * If it is realized on Friday that the version stored on Monday was incorrect, then a
 * correction may be applied. There are now two versions, the first of which has one correction.
 * This may continue, with multiple corrections allowed for each version.
 * <p>
 * Versions are represented by instants in the search.
 */
@PublicSPI
@BeanDefinition
public class PortfolioHistoryRequest extends AbstractHistoryRequest {

  /**
   * The depth of nodes to return.
   * A value of zero returns the root node, one returns the root node with immediate children, and so on.
   * A negative value, such as -1, returns the full tree.
   * By default this is -1.
   */
  @PropertyDefinition
  private int _depth = -1;

  /**
   * Creates an instance.
   * The object identifier must be added before searching.
   */
  public PortfolioHistoryRequest() {
    super();
  }

  /**
   * Creates an instance with object identifier.
   * This will retrieve all versions and corrections unless the relevant fields are set.
   * 
   * @param objectId  the object identifier, not null
   */
  public PortfolioHistoryRequest(final ObjectIdentifiable objectId) {
    super(objectId);
  }

  /**
   * Creates an instance with object identifier and optional version and correction.
   * 
   * @param objectId  the object identifier, not null
   * @param versionInstantProvider  the version instant to retrieve, null for all versions
   * @param correctedToInstantProvider  the instant that the data should be corrected to, null for all corrections
   */
  public PortfolioHistoryRequest(final ObjectIdentifiable objectId, InstantProvider versionInstantProvider, InstantProvider correctedToInstantProvider) {
    super(objectId, versionInstantProvider, correctedToInstantProvider);
  }

  //------------------------- AUTOGENERATED START -------------------------
  ///CLOVER:OFF
  /**
   * The meta-bean for {@code PortfolioHistoryRequest}.
   * @return the meta-bean, not null
   */
  public static PortfolioHistoryRequest.Meta meta() {
    return PortfolioHistoryRequest.Meta.INSTANCE;
  }
  static {
    JodaBeanUtils.registerMetaBean(PortfolioHistoryRequest.Meta.INSTANCE);
  }

  @Override
  public PortfolioHistoryRequest.Meta metaBean() {
    return PortfolioHistoryRequest.Meta.INSTANCE;
  }

  @Override
  protected Object propertyGet(String propertyName, boolean quiet) {
    switch (propertyName.hashCode()) {
      case 95472323:  // depth
        return getDepth();
    }
    return super.propertyGet(propertyName, quiet);
  }

  @Override
  protected void propertySet(String propertyName, Object newValue, boolean quiet) {
    switch (propertyName.hashCode()) {
      case 95472323:  // depth
        setDepth((Integer) newValue);
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
      PortfolioHistoryRequest other = (PortfolioHistoryRequest) obj;
      return JodaBeanUtils.equal(getDepth(), other.getDepth()) &&
          super.equals(obj);
    }
    return false;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash += hash * 31 + JodaBeanUtils.hashCode(getDepth());
    return hash ^ super.hashCode();
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the depth of nodes to return.
   * A value of zero returns the root node, one returns the root node with immediate children, and so on.
   * A negative value, such as -1, returns the full tree.
   * By default this is -1.
   * @return the value of the property
   */
  public int getDepth() {
    return _depth;
  }

  /**
   * Sets the depth of nodes to return.
   * A value of zero returns the root node, one returns the root node with immediate children, and so on.
   * A negative value, such as -1, returns the full tree.
   * By default this is -1.
   * @param depth  the new value of the property
   */
  public void setDepth(int depth) {
    this._depth = depth;
  }

  /**
   * Gets the the {@code depth} property.
   * A value of zero returns the root node, one returns the root node with immediate children, and so on.
   * A negative value, such as -1, returns the full tree.
   * By default this is -1.
   * @return the property, not null
   */
  public final Property<Integer> depth() {
    return metaBean().depth().createProperty(this);
  }

  //-----------------------------------------------------------------------
  /**
   * The meta-bean for {@code PortfolioHistoryRequest}.
   */
  public static class Meta extends AbstractHistoryRequest.Meta {
    /**
     * The singleton instance of the meta-bean.
     */
    static final Meta INSTANCE = new Meta();

    /**
     * The meta-property for the {@code depth} property.
     */
    private final MetaProperty<Integer> _depth = DirectMetaProperty.ofReadWrite(
        this, "depth", PortfolioHistoryRequest.class, Integer.TYPE);
    /**
     * The meta-properties.
     */
    private final Map<String, MetaProperty<Object>> _map = new DirectMetaPropertyMap(
      this, (DirectMetaPropertyMap) super.metaPropertyMap(),
        "depth");

    /**
     * Restricted constructor.
     */
    protected Meta() {
    }

    @Override
    protected MetaProperty<?> metaPropertyGet(String propertyName) {
      switch (propertyName.hashCode()) {
        case 95472323:  // depth
          return _depth;
      }
      return super.metaPropertyGet(propertyName);
    }

    @Override
    public BeanBuilder<? extends PortfolioHistoryRequest> builder() {
      return new DirectBeanBuilder<PortfolioHistoryRequest>(new PortfolioHistoryRequest());
    }

    @Override
    public Class<? extends PortfolioHistoryRequest> beanType() {
      return PortfolioHistoryRequest.class;
    }

    @Override
    public Map<String, MetaProperty<Object>> metaPropertyMap() {
      return _map;
    }

    //-----------------------------------------------------------------------
    /**
     * The meta-property for the {@code depth} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<Integer> depth() {
      return _depth;
    }

  }

  ///CLOVER:ON
  //-------------------------- AUTOGENERATED END --------------------------
}
