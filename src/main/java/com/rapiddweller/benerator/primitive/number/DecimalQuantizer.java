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

package com.rapiddweller.benerator.primitive.number;

import com.rapiddweller.common.ConversionException;
import com.rapiddweller.common.Converter;
import com.rapiddweller.common.converter.ThreadSafeConverter;

import java.math.BigDecimal;

/**
 * {@link Converter} that quantizes {@link Number}s by a given 'min' value and
 * 'granularity' and converts it into a {@link BigDecimal}.<br/><br/>
 * Created: 11.04.2011 17:53:55
 *
 * @author Volker Bergmann
 * @since 0.6.6
 */
public class DecimalQuantizer extends ThreadSafeConverter<Number, BigDecimal> {

  private final BigDecimal min;
  private final BigDecimal granularity;

  /**
   * Instantiates a new Decimal quantizer.
   *
   * @param min         the min
   * @param granularity the granularity
   */
  public DecimalQuantizer(BigDecimal min, BigDecimal granularity) {
    super(Number.class, BigDecimal.class);
    this.min = (min != null ? min : BigDecimal.ZERO);
    this.granularity = granularity;
  }

  @Override
  public BigDecimal convert(Number sourceValue) throws ConversionException {
    BigDecimal value = (sourceValue instanceof BigDecimal ? (BigDecimal) sourceValue :
        BigDecimal.valueOf(sourceValue.doubleValue()));
    BigDecimal ofs = value.subtract(min).divideToIntegralValue(granularity);
    return ofs.multiply(granularity).add(min);
  }

}
