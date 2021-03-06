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

package com.rapiddweller.benerator.engine.statement;

import com.rapiddweller.benerator.Generator;
import com.rapiddweller.benerator.engine.BeneratorContext;
import com.rapiddweller.benerator.wrapper.ProductWrapper;
import com.rapiddweller.common.Context;
import com.rapiddweller.common.ErrorHandler;
import com.rapiddweller.common.IOUtil;
import com.rapiddweller.script.Expression;
import com.rapiddweller.task.PageListener;
import com.rapiddweller.task.TaskExecutor;

import java.io.Closeable;
import java.util.ArrayList;
import java.util.List;

/**
 * Creates a number of entities in parallel execution and a given page size.<br/><br/>
 * Created: 01.02.2008 14:43:15
 *
 * @author Volker Bergmann
 */
public class GenerateOrIterateStatement extends AbstractStatement implements Closeable, PageListener {

  /**
   * The Task.
   */
  protected GenerateAndConsumeTask task;
  /**
   * The Count generator.
   */
  protected final Generator<Long> countGenerator;
  /**
   * The Min count.
   */
  protected final Expression<Long> minCount;
  /**
   * The Page size.
   */
  protected final Expression<Long> pageSize;
  /**
   * The Page listener ex.
   */
  protected final Expression<PageListener> pageListenerEx;
  /**
   * The Page listener.
   */
  protected PageListener pageListener;
  /**
   * The Info log.
   */
  protected final boolean infoLog;
  /**
   * The Is sub creator.
   */
  protected final boolean isSubCreator;
  /**
   * The Context.
   */
  protected final BeneratorContext context;
  /**
   * The Child context.
   */
  protected final BeneratorContext childContext;

  /**
   * Instantiates a new Generate or iterate statement.
   *
   * @param productName    the product name
   * @param countGenerator the count generator
   * @param minCount       the min count
   * @param pageSize       the page size
   * @param pageListenerEx the page listener ex
   * @param errorHandler   the error handler
   * @param infoLog        the info log
   * @param isSubCreator   the is sub creator
   * @param context        the context
   */
  public GenerateOrIterateStatement(String productName, Generator<Long> countGenerator, Expression<Long> minCount,
                                    Expression<Long> pageSize, Expression<PageListener> pageListenerEx,
                                    Expression<ErrorHandler> errorHandler, boolean infoLog, boolean isSubCreator, BeneratorContext context) {
    this.task = null;
    this.countGenerator = countGenerator;
    this.minCount = minCount;
    this.pageSize = pageSize;
    this.pageListenerEx = pageListenerEx;
    this.infoLog = infoLog;
    this.isSubCreator = isSubCreator;
    this.context = context;
    this.childContext = context.createSubContext(productName);
  }

  /**
   * Sets task.
   *
   * @param task the task
   */
  public void setTask(GenerateAndConsumeTask task) {
    this.task = task;
  }

  /**
   * Gets task.
   *
   * @return the task
   */
  public GenerateAndConsumeTask getTask() {
    return task;
  }

  /**
   * Gets context.
   *
   * @return the context
   */
  public BeneratorContext getContext() {
    return context;
  }

  /**
   * Gets child context.
   *
   * @return the child context
   */
  public BeneratorContext getChildContext() {
    return childContext;
  }


  // Statement interface ---------------------------------------------------------------------------------------------

  @Override
  public boolean execute(BeneratorContext ctx) {
    if (!beInitialized(ctx)) {
      task.reset();
    }
    executeTask(generateCount(childContext), minCount.evaluate(childContext), pageSize.evaluate(childContext),
        evaluatePageListeners(childContext), getErrorHandler(childContext));
    if (!isSubCreator) {
      close();
    }
    return true;
  }

  /**
   * Generate count long.
   *
   * @param context the context
   * @return the long
   */
  public Long generateCount(BeneratorContext context) {
    beInitialized(context);
    ProductWrapper<Long> count = countGenerator.generate(new ProductWrapper<>());
    return (count != null ? count.unwrap() : null);
  }

  @Override
  public void close() {
    task.close();
    countGenerator.close();
    if (pageListener instanceof Closeable) {
      IOUtil.close((Closeable) pageListener);
    }
  }

  // PageListener interface implementation ---------------------------------------------------------------------------

  @Override
  public void pageStarting() {
  }

  @Override
  public void pageFinished() {
    getTask().pageFinished();
  }


  // internal helpers ------------------------------------------------------------------------------------------------

  /**
   * Evaluate page listeners list.
   *
   * @param context the context
   * @return the list
   */
  protected List<PageListener> evaluatePageListeners(Context context) {
    List<PageListener> listeners = new ArrayList<>();
    if (pageListener != null) {
      pageListener = pageListenerEx.evaluate(context);
      if (pageListener != null) {
        listeners.add(pageListener);
      }
    }
    return listeners;
  }

  /**
   * Be initialized boolean.
   *
   * @param context the context
   * @return the boolean
   */
  protected boolean beInitialized(BeneratorContext context) {
    if (!countGenerator.wasInitialized()) {
      countGenerator.init(childContext);
      task.init(childContext);
      return true;
    }
    return false;
  }

  /**
   * Execute task.
   *
   * @param requestedExecutions the requested executions
   * @param minExecutions       the min executions
   * @param pageSizeValue       the page size value
   * @param pageListeners       the page listeners
   * @param errorHandler        the error handler
   */
  protected void executeTask(Long requestedExecutions, Long minExecutions, Long pageSizeValue,
                             List<PageListener> pageListeners, ErrorHandler errorHandler) {
    TaskExecutor.execute(task, childContext, requestedExecutions, minExecutions,
        pageListeners, pageSizeValue, false, errorHandler, infoLog);
  }

}
