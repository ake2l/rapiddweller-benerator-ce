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

package com.rapiddweller.benerator.sample;

import com.rapiddweller.benerator.IllegalGeneratorStateException;
import com.rapiddweller.benerator.InvalidGeneratorSetupException;
import com.rapiddweller.benerator.util.WrapperProvider;
import com.rapiddweller.benerator.wrapper.ProductWrapper;
import com.rapiddweller.common.BeanUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * Counts frequencies of atoms and provides random atoms with the same frequency.<br/>
 * <br/>
 * Created at 12.07.2009 07:51:04
 *
 * @param <E> the type parameter
 * @author Volker Bergmann
 * @since 0.6.0
 */
public class SeedManager<E> {

  private final Map<E, SeedManager<E>> successors;
  private int weight;
  private final int depth;
  private boolean initialized;
  private AttachedWeightSampleGenerator<E> helper;
  private final Class<E> generatedType;
  private final WrapperProvider<E> wrapperProvider;

  // constructor and properties --------------------------------------------------------------------------------------

  /**
   * Instantiates a new Seed manager.
   *
   * @param generatedType the generated type
   * @param depth         the depth
   */
  public SeedManager(Class<E> generatedType, int depth) {
    this.generatedType = generatedType;
    this.weight = 0;
    this.depth = depth;
    this.successors = new HashMap<>();
    this.wrapperProvider = new WrapperProvider<>();
  }

  /**
   * Gets depth.
   *
   * @return the depth
   */
  public int getDepth() {
    return depth;
  }

  /**
   * Gets weight.
   *
   * @return the weight
   */
  public double getWeight() {
    return weight;
  }

  // functional interface --------------------------------------------------------------------------------------------

  /**
   * Gets generated type.
   *
   * @return the generated type
   */
  public Class<E> getGeneratedType() {
    return generatedType;
  }

  /**
   * Add sequence.
   *
   * @param startIndex the start index
   * @param sequence   the sequence
   */
  @SafeVarargs
  public final void addSequence(int startIndex, E... sequence) {
    weight++;
    if (depth > 0) {
      getSuccessor(sequence[startIndex]).addSequence(startIndex + 1, sequence);
    }
  }

  /**
   * Init.
   */
  public void init() {
    if (initialized) {
      throw new IllegalGeneratorStateException("Already initialized: " + this);
    }
    if (getWeight() == 0) {
      throw new InvalidGeneratorSetupException(getClass().getSimpleName() + " is empty");
    }
    helper = new AttachedWeightSampleGenerator<>(generatedType);
    for (Map.Entry<E, SeedManager<E>> entry : successors.entrySet()) {
      helper.addSample(entry.getKey(), entry.getValue().getWeight());
    }
    helper.init(null);
  }

  /**
   * Random atom e.
   *
   * @return the e
   */
  public E randomAtom() {
    if (!initialized) {
      init();
    }
    return helper.generate(getWrapper()).unwrap();
  }

  /**
   * Gets successor.
   *
   * @param atom the atom
   * @return the successor
   */
  public SeedManager<E> getSuccessor(E atom) {
    SeedManager<E> result = successors.get(atom);
    if (result == null) {
      result = new SeedManager<>(generatedType, depth - 1);
      successors.put(atom, result);
    }
    return result;
  }

  /**
   * Print state.
   */
  public void printState() {
    printState("");
  }

  /**
   * Print state.
   *
   * @param indent the indent
   */
  public void printState(String indent) {
    for (Map.Entry<E, SeedManager<E>> entry : successors.entrySet()) {
      SeedManager<E> successor = entry.getValue();
      System.out.println(indent + entry.getKey() + '[' + successor.getWeight() + ']');
      successor.printState("  " + indent);
    }
  }

  private ProductWrapper<E> getWrapper() {
    return wrapperProvider.get();
  }

  // java.lang.Object overrides --------------------------------------------------------------------------------------

  @Override
  public String toString() {
    return BeanUtil.toString(this, true);
  }

}
