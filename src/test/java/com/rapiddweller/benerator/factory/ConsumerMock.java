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

package com.rapiddweller.benerator.factory;

import com.rapiddweller.benerator.consumer.AbstractConsumer;
import com.rapiddweller.common.Context;
import com.rapiddweller.common.context.ContextAware;
import com.rapiddweller.model.data.Entity;

import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * The type Consumer mock.
 */
public class ConsumerMock extends AbstractConsumer implements ContextAware {

  /**
   * The constant instances.
   */
  public static final HashMap<Integer, ConsumerMock> instances
      = new HashMap<>();
  private final Random random = new Random();

  /**
   * The constant lastInstance.
   */
  public static ConsumerMock lastInstance;

  /**
   * The Last product.
   */
  public Object lastProduct;
  /**
   * The Id.
   */
  public final int id;
  /**
   * The Invocation count.
   */
  public final AtomicInteger invocationCount;
  /**
   * The Closed.
   */
  public boolean closed;
  /**
   * The Context.
   */
  public Context context;

  /**
   * Instantiates a new Consumer mock.
   */
  public ConsumerMock() {
    this(1);
  }

  /**
   * Instantiates a new Consumer mock.
   *
   * @param id the id
   */
  public ConsumerMock(int id) {
    this.id = id;
    this.invocationCount = new AtomicInteger();
    this.closed = false;
    lastInstance = this;
    instances.put(id, this);
  }

  @Override
  public void setContext(Context context) {
    this.context = context;
  }

  @Override
  public void startProductConsumption(Object object) {
    lastProduct = object;
    invocationCount.incrementAndGet();
  }

  @Override
  public void close() {
    super.close();
    closed = true;
  }

  /**
   * Enrol customer.
   *
   * @param name the name
   * @param age  the age
   * @throws Exception the exception
   */
  public void enrolCustomer(String name, int age) throws Exception {
    lastProduct = new Entity("Person", null, "name", name, "age", age);
    System.out.println("enrolled: " + name + " (" + age + ") - " + Thread.currentThread().getName());
    Thread.sleep(50 + random.nextInt(100));
  }

}