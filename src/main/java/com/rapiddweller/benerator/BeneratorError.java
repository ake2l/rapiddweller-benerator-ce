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

package com.rapiddweller.benerator;

/**
 * Indicates an error in Benerator execution, for example raised by an &lt;error&gt; element.<br/><br/>
 * Created: 12.01.2011 09:13:11
 *
 * @author Volker Bergmann
 * @since 0.6.4
 */
public class BeneratorError extends RuntimeException {

  private static final long serialVersionUID = 4922982624810176934L;

  /**
   * The Code.
   */
  protected final int code;

  /**
   * Instantiates a new Benerator error.
   *
   * @param s         the s
   * @param throwable the throwable
   * @param code      the code
   */
  public BeneratorError(String s, Throwable throwable, int code) {
    super(s, throwable);
    this.code = code;
  }

  /**
   * Instantiates a new Benerator error.
   *
   * @param s    the s
   * @param code the code
   */
  public BeneratorError(String s, int code) {
    super(s);
    this.code = code;
  }

  /**
   * Instantiates a new Benerator error.
   *
   * @param throwable the throwable
   * @param code      the code
   */
  public BeneratorError(Throwable throwable, int code) {
    super(throwable);
    this.code = code;
  }

  /**
   * Gets code.
   *
   * @return the code
   */
  public int getCode() {
    return code;
  }

}
