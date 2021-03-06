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

package com.rapiddweller.benerator.anno;

import com.rapiddweller.model.data.Format;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Specifies a data source and its characteristics for generating data for the annotated element.<br/><br/>
 * Created: 02.05.2010 19:01:59
 *
 * @author Volker Bergmann
 * @since 0.6.1
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.CONSTRUCTOR})
public @interface Source {
  /**
   * Value string.
   *
   * @return the string
   */
  String value() default "";

  /**
   * Id string.
   *
   * @return the string
   */
  String id() default "";

  /**
   * Selector string.
   *
   * @return the string
   */
  String selector() default "";

  /**
   * Uri string.
   *
   * @return the string
   */
  String uri() default "";

  /**
   * Segment string.
   *
   * @return the string
   */
  String segment() default "";

  /**
   * Format format.
   *
   * @return the format
   */
  Format format() default Format.globalDefault;

  /**
   * Filter string.
   *
   * @return the string
   */
  String filter() default "";

  /**
   * Separator string.
   *
   * @return the string
   */
  String separator() default "";

  /**
   * Dataset string.
   *
   * @return the string
   */
  String dataset() default "";

  /**
   * Nesting string.
   *
   * @return the string
   */
  String nesting() default "";

  /**
   * Encoding string.
   *
   * @return the string
   */
  String encoding() default "";

  /**
   * Empty marker string.
   *
   * @return the string
   */
  String emptyMarker() default "";

  /**
   * Null marker string.
   *
   * @return the string
   */
  String nullMarker() default "";

  /**
   * Row based boolean.
   *
   * @return the boolean
   */
  boolean rowBased() default true;
}
