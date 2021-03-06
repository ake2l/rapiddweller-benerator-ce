/*
 * (c) Copyright 2006-2020 by rapiddweller GmbH & Volker Bergmann. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, is permitted under the terms of the
 * GNU General Public License.
 *
 * For redistributing this software or a derivative work under a license other
 * than the GPL-compatible Free Software License as defined by the Free
 * Software Foundation or approved by OSI, you must first obtain a commercial
 * license to this software product from rapiddweller GmbH & Volker Bergmann.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * WITHOUT A WARRANTY OF ANY KIND. ALL EXPRESS OR IMPLIED CONDITIONS,
 * REPRESENTATIONS AND WARRANTIES, INCLUDING ANY IMPLIED WARRANTY OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE
 * HEREBY EXCLUDED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

package com.rapiddweller.benerator.test;

import com.rapiddweller.benerator.Generator;
import com.rapiddweller.benerator.engine.DefaultBeneratorContext;
import com.rapiddweller.benerator.primitive.number.AbstractNonNullNumberGenerator;
import com.rapiddweller.benerator.wrapper.ProductWrapper;
import com.rapiddweller.common.ArrayFormat;
import com.rapiddweller.common.ArrayUtil;
import com.rapiddweller.common.BeanUtil;
import com.rapiddweller.common.CollectionUtil;
import com.rapiddweller.common.Converter;
import com.rapiddweller.common.IOUtil;
import com.rapiddweller.common.Resettable;
import com.rapiddweller.common.Validator;
import com.rapiddweller.common.collection.ObjectCounter;
import com.rapiddweller.common.converter.ToStringConverter;
import com.rapiddweller.common.validator.UniqueValidator;
import com.rapiddweller.model.data.Entity;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Provides methods for testing generators.<br/>
 * <br/>
 * Created: 15.11.2007 14:46:31
 *
 * @author Volker Bergmann
 */
public abstract class GeneratorTest extends ModelTest {

  private final Converter<Object, String> formatter = new ToStringConverter();

  // helper methods for this and child classes -----------------------------------------------------------------------

  /**
   * Initialize t.
   *
   * @param <T>       the type parameter
   * @param <U>       the type parameter
   * @param generator the generator
   * @return the t
   */
  public <T extends Generator<U>, U> T initialize(T generator) {
    generator.init(context);
    return generator;
  }

  /**
   * Close.
   *
   * @param generator the generator
   */
  public void close(Generator<?> generator) {
    IOUtil.close(generator);
  }

  /**
   * Sets current product.
   *
   * @param product     the product
   * @param productName the product name
   */
  public void setCurrentProduct(Object product, String productName) {
    ((DefaultBeneratorContext) context).setCurrentProduct(new ProductWrapper<>(product), productName);
  }

  /**
   * Print products.
   *
   * @param generator the generator
   * @param n         the n
   */
  @SuppressWarnings({"unchecked", "rawtypes"})
  public void printProducts(Generator<?> generator, int n) {
    ProductWrapper wrapper = new ProductWrapper();
    for (int i = 0; i < n; i++) {
      ProductWrapper<?> tmp = generator.generate(wrapper);
      if (tmp == null) {
        System.out.println("<>");
      } else {
        System.out.println(formatter.convert(tmp.unwrap()));
      }
    }
  }

  /**
   * Count products map.
   *
   * @param <T>       the type parameter
   * @param generator the generator
   * @param n         the n
   * @return the map
   */
  public static <T> Map<T, AtomicInteger> countProducts(Generator<T> generator, int n) {
    ObjectCounter<T> counter = new ObjectCounter<>(Math.min(n, 1000));
    ProductWrapper<T> wrapper = new ProductWrapper<>();
    for (int i = 0; i < n; i++) {
      wrapper = generator.generate(wrapper);
      if (wrapper == null) {
        fail("Generator unavailable after " + i + " of " + n + " invocations");
      } else {
        counter.count(wrapper.unwrap());
      }
    }
    return counter.getCounts();
  }

  /**
   * Assert equal arrays.
   *
   * @param <T>      the type parameter
   * @param expected the expected
   * @param actual   the actual
   */
  protected static <T> void assertEqualArrays(T expected, T actual) {
    ArrayFormat format = new ArrayFormat();
    assertTrue("Expected " + format.format(expected) + ", found: " + format.format(actual),
        ArrayUtil.equals(expected, actual));
  }

  /**
   * Expect generated sequence helper.
   *
   * @param <T>       the type parameter
   * @param generator the generator
   * @param products  the products
   * @return the helper
   */
  @SafeVarargs
  protected static <T> Helper expectGeneratedSequence(Generator<T> generator, T... products) {
    expectGeneratedSequenceOnce(generator, products);
    generator.reset();
    expectGeneratedSequenceOnce(generator, products);
    return new Helper(generator);
  }

  /**
   * Expect generated set helper.
   *
   * @param <T>         the type parameter
   * @param generator   the generator
   * @param invocations the invocations
   * @param products    the products
   * @return the helper
   */
  @SafeVarargs
  protected final <T> Helper expectGeneratedSet(Generator<T> generator, int invocations, T... products) {
    expectGeneratedSetOnce(generator, invocations, products);
    generator.reset();
    expectGeneratedSetOnce(generator, invocations, products);
    return new Helper(generator);
  }

  /**
   * Expect uniquely generated set helper.
   *
   * @param <T>       the type parameter
   * @param generator the generator
   * @param products  the products
   * @return the helper
   */
  @SafeVarargs
  protected final <T> Helper expectUniquelyGeneratedSet(Generator<T> generator, T... products) {
    expectUniquelyGeneratedSetOnce(generator, products);
    generator.reset();
    expectUniquelyGeneratedSetOnce(generator, products);
    return new Helper(generator);
  }

  /**
   * Expect unique products helper.
   *
   * @param <T>       the type parameter
   * @param generator the generator
   * @param n         the n
   * @return the helper
   */
  protected <T> Helper expectUniqueProducts(Generator<T> generator, int n) {
    expectUniqueProductsOnce(generator, n);
    generator.reset();
    expectUniqueProductsOnce(generator, n);
    return new Helper(generator);
  }

  /**
   * Expect generations helper.
   *
   * @param <T>        the type parameter
   * @param generator  the generator
   * @param n          the n
   * @param validators the validators
   * @return the helper
   */
  @SuppressWarnings({"unchecked", "rawtypes"})
  protected <T> Helper expectGenerations(Generator<T> generator, int n, Validator... validators) {
    expectGenerationsOnce(generator, n, validators);
    generator.reset();
    for (Validator<?> validator : validators) {
      if (validator instanceof Resettable) {
        ((Resettable) validator).reset();
      }
    }
    expectGenerationsOnce(generator, n, validators);
    return new Helper(generator);
  }

  /**
   * Expect unique generations helper.
   *
   * @param <T>       the type parameter
   * @param generator the generator
   * @param n         the n
   * @return the helper
   */
  protected <T> Helper expectUniqueGenerations(Generator<T> generator, int n) {
    expectUniqueGenerationsOnce(generator, n);
    generator.reset();
    expectUniqueGenerationsOnce(generator, n);
    return new Helper(generator);
  }

  /**
   * Expect range.
   *
   * @param <T>       the type parameter
   * @param generator the generator
   * @param n         the n
   * @param min       the min
   * @param max       the max
   */
  protected <T extends Comparable<T>> void expectRange(Generator<T> generator, int n, T min, T max) {
    expectRangeOnce(generator, n, min, max);
    generator.reset();
    expectRangeOnce(generator, n, min, max);
    new Helper(generator);
  }

  /**
   * Format string.
   *
   * @param <T>     the type parameter
   * @param product the product
   * @return the string
   */
  protected <T> String format(T product) {
    return ToStringConverter.convert(product, "[null]");
  }

  /**
   * Assert unavailable.
   *
   * @param generator the generator
   */
  @SuppressWarnings({"rawtypes", "unchecked"})
  public static void assertUnavailable(Generator<?> generator) {
    assertNull("Generator " + generator + " is expected to be unavailable", generator.generate(new ProductWrapper()));
  }

  /**
   * Assert available.
   *
   * @param generator the generator
   */
  public static void assertAvailable(Generator<?> generator) {
    assertAvailable("Generator " + generator + " is expected to be available", generator);
  }

  /**
   * Assert available.
   *
   * @param message   the message
   * @param generator the generator
   */
  @SuppressWarnings({"rawtypes", "unchecked"})
  public static void assertAvailable(String message, Generator<?> generator) {
    assertNotNull(message, generator.generate(new ProductWrapper()));
  }

  // Number generator tests ------------------------------------------------------------------------------------------

  /**
   * Check equal distribution.
   *
   * @param <T>            the type parameter
   * @param generatorClass the generator class
   * @param min            the min
   * @param max            the max
   * @param granularity    the granularity
   * @param iterations     the iterations
   * @param tolerance      the tolerance
   * @param expectedValues the expected values
   */
  @SafeVarargs
  public static <T extends Number> void checkEqualDistribution(
      Class<? extends AbstractNonNullNumberGenerator<T>> generatorClass, T min, T max, T granularity,
      int iterations, double tolerance, T... expectedValues) {
    Set<T> expectedSet = CollectionUtil.toSet(expectedValues);
    checkDistribution(generatorClass, min, max, granularity, iterations, true, tolerance, expectedSet);
  }

  /**
   * Check equal distribution.
   *
   * @param <T>            the type parameter
   * @param generatorClass the generator class
   * @param min            the min
   * @param max            the max
   * @param granularity    the granularity
   * @param iterations     the iterations
   * @param tolerance      the tolerance
   * @param expectedSet    the expected set
   */
  public static <T extends Number> void checkEqualDistribution(
      Class<? extends AbstractNonNullNumberGenerator<T>> generatorClass, T min, T max, T granularity,
      int iterations, double tolerance, Set<T> expectedSet) {
    checkDistribution(generatorClass, min, max, granularity, iterations, true, tolerance, expectedSet);
  }

  private static <T extends Number> void checkDistribution(
      Class<? extends AbstractNonNullNumberGenerator<T>> generatorClass, T min, T max, T granularity,
      int iterations, boolean equalDistribution, double tolerance, Set<T> expectedSet) {
    AbstractNonNullNumberGenerator<T> generator = BeanUtil.newInstance(generatorClass);
    generator.setMin(min);
    generator.setMax(max);
    generator.setGranularity(granularity);
    ObjectCounter<T> counter = new ObjectCounter<>(expectedSet != null ? expectedSet.size() : 10);
    ProductWrapper<T> wrapper = new ProductWrapper<>();
    for (int i = 0; i < iterations; i++) {
      counter.count(generator.generate(wrapper).unwrap());
    }
    checkDistribution(counter, equalDistribution, tolerance, expectedSet);
  }

  // unspecific generator tests --------------------------------------------------------------------------------------

  /**
   * Check equal distribution.
   *
   * @param <E>         the type parameter
   * @param generator   the generator
   * @param iterations  the iterations
   * @param tolerance   the tolerance
   * @param expectedSet the expected set
   */
  public static <E> void checkEqualDistribution(
      Generator<E> generator, int iterations, double tolerance, Set<E> expectedSet) {
    checkDistribution(generator, iterations, true, tolerance, expectedSet);
  }

  /**
   * Check product set.
   *
   * @param <T>         the type parameter
   * @param generator   the generator
   * @param iterations  the iterations
   * @param expectedSet the expected set
   */
  public static <T> void checkProductSet(Generator<T> generator, int iterations, Set<T> expectedSet) {
    checkDistribution(generator, iterations, false, 0, expectedSet);
  }

  private static <T> void checkDistribution(Generator<T> generator,
                                            int iterations, boolean equalDistribution, double tolerance, Set<T> expectedSet) {
    ObjectCounter<T> counter = new ObjectCounter<>(expectedSet != null ? expectedSet.size() : 10);
    ProductWrapper<T> wrapper = new ProductWrapper<>();
    for (int i = 0; i < iterations; i++) {
      counter.count(generator.generate(wrapper).unwrap());
    }
    checkDistribution(counter, equalDistribution, tolerance, expectedSet);
  }

  /**
   * Expect relative weights.
   *
   * @param generator                the generator
   * @param iterations               the iterations
   * @param expectedValueWeightPairs the expected value weight pairs
   */
  @SuppressWarnings({"rawtypes", "unchecked"})
  protected static void expectRelativeWeights(Generator<?> generator, int iterations, Object... expectedValueWeightPairs) {
    ObjectCounter<Object> counter = new ObjectCounter<>(expectedValueWeightPairs.length / 2);
    ProductWrapper wrapper = new ProductWrapper();
    for (int i = 0; i < iterations; i++) {
      wrapper = generator.generate(wrapper);
      if (wrapper == null) {
        fail("Generator unavailable after " + i + " of " + iterations + " invocations");
      } else {
        counter.count(wrapper.unwrap());
      }
    }
    Set<Object> productSet = counter.objectSet();
    double totalExpectedWeight = 0;
    for (int i = 1; i < expectedValueWeightPairs.length; i += 2) {
      totalExpectedWeight += ((Number) expectedValueWeightPairs[i]).doubleValue();
    }

    for (int i = 0; i < expectedValueWeightPairs.length; i += 2) {
      Object value = expectedValueWeightPairs[i];
      double expectedWeight = ((Number) expectedValueWeightPairs[i + 1]).doubleValue() / totalExpectedWeight;
      if (expectedWeight > 0) {
        assertTrue("Generated set does not contain value " + value, productSet.contains(value));
        double measuredWeight = counter.getRelativeCount(value);
        assertTrue("For value '" + value + "', weight " + expectedWeight + " is expected, but it is " + measuredWeight,
            Math.abs(measuredWeight - expectedWeight) / expectedWeight < 0.15);
      } else {
        assertFalse("Generated contains value " + value + " though it has zero weight", productSet.contains(value));
      }
    }
  }

  // collection checks -----------------------------------------------------------------------------------------------

  /**
   * Check equal distribution.
   *
   * @param <E>         the type parameter
   * @param collection  the collection
   * @param tolerance   the tolerance
   * @param expectedSet the expected set
   */
  public static <E> void checkEqualDistribution(Collection<E> collection, double tolerance, Set<E> expectedSet) {
    checkDistribution(collection, true, tolerance, expectedSet);
  }

  private static <E> void checkDistribution(Collection<E> collection,
                                            boolean equalDistribution, double tolerance, Set<E> expectedSet) {
    ObjectCounter<E> counter = new ObjectCounter<>(expectedSet != null ? expectedSet.size() : 10);
    for (E object : collection) {
      counter.count(object);
    }
    checkDistribution(counter, equalDistribution, tolerance, expectedSet);
  }

  // counter checks --------------------------------------------------------------------------------------------------

  /**
   * Check equal distribution.
   *
   * @param <E>         the type parameter
   * @param counter     the counter
   * @param tolerance   the tolerance
   * @param expectedSet the expected set
   */
  public static <E> void checkEqualDistribution(
      ObjectCounter<E> counter, double tolerance, Set<E> expectedSet) {
    checkDistribution(counter, true, tolerance, expectedSet);
  }

  private static <E> void checkDistribution(
      ObjectCounter<E> counter, boolean equalDistribution, double tolerance, Set<E> expectedSet) {
    if (equalDistribution) {
      assertTrue("Distribution is not equal: " + counter, counter.equalDistribution(tolerance));
    }
    if (expectedSet != null) {
      assertEquals(expectedSet, counter.objectSet());
    }
  }

  /**
   * The type Helper.
   */
  public static class Helper {
    private final Generator<?> generator;

    /**
     * Instantiates a new Helper.
     *
     * @param generator the generator
     */
    public Helper(Generator<?> generator) {
      this.generator = generator;
    }

    /**
     * With ceased availability.
     */
    public void withCeasedAvailability() {
      try {
        assertUnavailable(generator);
      } finally {
        generator.close();
      }
    }

    /**
     * With continued availability.
     */
    public void withContinuedAvailability() {
      assertAvailable(generator);
    }
  }

  // private helpers -------------------------------------------------------------------------------------------------

  /**
   * Expect generated sequence once.
   *
   * @param <T>       the type parameter
   * @param generator the generator
   * @param products  the products
   */
  @SafeVarargs
  protected static <T> void expectGeneratedSequenceOnce(Generator<T> generator, T... products) {
    int count = 0;
    ProductWrapper<T> wrapper = new ProductWrapper<>();
    for (T expectedProduct : products) {
      wrapper = generator.generate(wrapper);
      assertNotNull("Generator is unavailable after generating " + count + " of " + products.length + " products: " + generator, wrapper);
      T generatedProduct = wrapper.unwrap();
      if (generatedProduct.getClass().isArray()) {
        assertEqualArrays(expectedProduct, generatedProduct);
      } else {
        assertEquals(expectedProduct, generatedProduct);
      }
      count++;
    }
  }

  @SafeVarargs
  private <T> void expectGeneratedSetOnce(Generator<T> generator, int invocations, T... expectedProducts) {
    Set<T> expectedSet = CollectionUtil.toSet(expectedProducts);
    Set<T> observedSet = new HashSet<>(expectedProducts.length);
    ProductWrapper<T> wrapper = new ProductWrapper<>();
    for (int i = 0; i < invocations; i++) {
      wrapper = generator.generate(wrapper);
      assertNotNull("Generator has gone unavailable. " +
              "Generated only " + i + " of " + expectedProducts.length + " expected values: " + observedSet,
          wrapper);
      T generation = wrapper.unwrap();
      logger.debug("created " + format(generation));
      assertTrue("The generated value '" + format(generation) + "' was not in the expected set: " + expectedSet,
          expectedSet.contains(generation));
      observedSet.add(generation);
    }
    assertEquals(expectedSet, observedSet);
  }

  @SafeVarargs
  private <T> void expectUniquelyGeneratedSetOnce(Generator<T> generator, T... expectedProducts) {
    Set<T> expectedSet = CollectionUtil.toSet(expectedProducts);
    UniqueValidator<Object> validator = new UniqueValidator<>();
    ProductWrapper<T> wrapper = new ProductWrapper<>();
    for (int i = 0; i < expectedProducts.length; i++) {
      wrapper = generator.generate(wrapper);
      assertNotNull("Generator has gone unavailable after " + i + " products, " +
          "expected " + expectedProducts.length + " products. ", wrapper);
      T product = wrapper.unwrap();
      logger.debug("created " + format(product));
      assertTrue("Product is not unique: " + product, validator.valid(product));
      assertTrue("The generated value '" + format(product) + "' was not in the expected set: "
          + format(expectedSet), expectedSet.contains(product));
    }
  }

  private <T> void expectUniqueProductsOnce(Generator<T> generator, int n) {
    UniqueValidator<T> validator = new UniqueValidator<>();
    ProductWrapper<T> wrapper = new ProductWrapper<>();
    for (int i = 0; i < n; i++) {
      wrapper = generator.generate(wrapper);
      assertNotNull("Generator is not available: " + generator, wrapper);
      T product = wrapper.unwrap();
      logger.debug("created: " + format(product));
      assertTrue("Product is not unique: " + product, validator.valid(product));
    }
  }

  @SafeVarargs
  private <T> void expectGenerationsOnce(Generator<T> generator, int n, Validator<T>... validators) {
    ProductWrapper<T> wrapper = new ProductWrapper<>();
    for (int i = 0; i < n; i++) {
      wrapper = generator.generate(wrapper);
      assertNotNull("Generator has gone unavailable before creating the required number of products, " +
              "required " + n + " but was " + i,
          wrapper);
      T product = wrapper.unwrap();
      logger.debug("created " + format(product));
      for (Validator<T> validator : validators) {
        assertTrue("The generated value '" + format(product) + "' is not valid according to " + validator +
                ", failed after " + i + " generations",
            validator.valid(product));
      }
    }
  }

  @SafeVarargs
  private <T> void expectUniqueGenerationsOnce(Generator<T> generator, int n, Validator<T>... validators) {
    UniqueValidator<T> validator = new UniqueValidator<>();
    ProductWrapper<T> wrapper = new ProductWrapper<>();
    for (int i = 0; i < n; i++) {
      wrapper = generator.generate(wrapper);
      assertNotNull("Generator has gone unavailable before creating the required number of products ",
          wrapper);
      T product = wrapper.unwrap();
      logger.debug("created " + format(product));
      assertTrue("The generated value '" + format(product) + "' is not unique. Generator is " + generator,
          validator.valid(product));
    }
  }

  /**
   * Expect range once.
   *
   * @param <T>       the type parameter
   * @param generator the generator
   * @param n         the n
   * @param min       the min
   * @param max       the max
   */
  protected <T extends Comparable<T>> void expectRangeOnce(Generator<T> generator, int n, T min, T max) {
    ProductWrapper<T> wrapper = new ProductWrapper<>();
    for (int i = 0; i < n; i++) {
      wrapper = generator.generate(wrapper);
      assertNotNull(wrapper);
      T product = wrapper.unwrap();
      assertTrue("Generated value (" + product + ") is less than the configured minimum (" + min + ")", min.compareTo(product) <= 0);
      assertTrue("Generated value (" + product + ") is greater than the configured maximum (" + max + ")", max.compareTo(product) >= 0);
    }
  }

  /**
   * Assert components.
   *
   * @param entity         the entity
   * @param nameValuePairs the name value pairs
   */
  public static void assertComponents(Entity entity, Object... nameValuePairs) {
    for (int i = 0; i < nameValuePairs.length; i += 2) {
      String name = (String) nameValuePairs[i];
      Object expected = nameValuePairs[i + 1];
      Object actual = entity.getComponent(name);
      assertEquals("Unexpected value for component '" + name + "':", expected, actual);
    }
  }

}
