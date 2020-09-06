package com.vzome.jsweet;

import com.vzome.core.algebra.AlgebraicField;
import com.vzome.core.algebra.AlgebraicNumber;

public class JsAlgebraicNumber implements AlgebraicNumber
{
    private final JsAlgebraicField field;
    private final int[] factors;

    public JsAlgebraicNumber( JsAlgebraicField field, int[] factors )
    {
        this .field = field;
        this .factors = factors;
    }

    @Override
    public AlgebraicField getField()
    {
        return this .field;
    }

    @Override
    public double evaluate()
    {
        return this .field .evaluateNumber( this .factors );
    }

    @Override
    public int[] toTrailingDivisor()
    {
        return this .factors;
    }

    /**
     * 
     * @param n is the value to be added
     * @return this + n
     */
    public AlgebraicNumber plus( int n )
    {
        return n == 0 ? this : this.plus( field.createRational(n) );
    }

    /**
     * 
     * @param num is the numerator of the rational value to be added
     * @param den is the denominator of the rational value to be added
     * @return this + (num / den)
     */
    public AlgebraicNumber plus( int num, int den )
    {
        return this.plus( field.createRational( num, den ) );
    }

    @Override
    public AlgebraicNumber plus( AlgebraicNumber that )
    {
        int[] factors = this.field .add( this.factors, ((JsAlgebraicNumber) that) .factors );
        return new JsAlgebraicNumber( this .field, factors );
    }

    @Override
    public AlgebraicNumber times( AlgebraicNumber that )
    {
        int[] factors = this.field .multiply( this.factors, ((JsAlgebraicNumber) that) .factors );
        return new JsAlgebraicNumber( this .field, factors );
    }

    @Override
    public boolean isZero()
    {
        for ( int i = 0; i < factors.length-1; i++ ) {
            if ( factors[ i ] != 0 )
                return false;
        }
        return true;
    }

    @Override
    public boolean isOne()
    {
        if ( factors[ 0 ] != 1 )
            return false;
        if ( factors[ factors.length-1 ] != 1 )
            return false;
        for ( int i = 1; i < factors.length-1; i++ ) {
            if ( factors[ i ] != 0 )
                return false;
        }
        return true;
    }
    
    
    
    @Override
    public int compareTo(AlgebraicNumber o)
    {
        throw new RuntimeException( "unimplemented" );
    }

    @Override
    public boolean greaterThan(AlgebraicNumber other)
    {
        throw new RuntimeException( "unimplemented" );
    }

    @Override
    public boolean lessThan(AlgebraicNumber other)
    {
        throw new RuntimeException( "unimplemented" );
    }

    @Override
    public boolean greaterThanOrEqualTo(AlgebraicNumber other)
    {
        throw new RuntimeException( "unimplemented" );
    }

    @Override
    public boolean lessThanOrEqualTo(AlgebraicNumber other)
    {
        throw new RuntimeException( "unimplemented" );
    }

    @Override
    public AlgebraicNumber times(int n)
    {
        throw new RuntimeException( "unimplemented" );
    }

    @Override
    public AlgebraicNumber times(int num, int den)
    {
        throw new RuntimeException( "unimplemented" );
    }

    @Override
    public AlgebraicNumber minus(int n)
    {
        throw new RuntimeException( "unimplemented" );
    }

    @Override
    public AlgebraicNumber minus(int num, int den)
    {
        throw new RuntimeException( "unimplemented" );
    }

    @Override
    public AlgebraicNumber minus(AlgebraicNumber that)
    {
        throw new RuntimeException( "unimplemented" );
    }

    @Override
    public AlgebraicNumber dividedBy(int divisor)
    {
        throw new RuntimeException( "unimplemented" );
    }

    @Override
    public AlgebraicNumber dividedBy(int num, int den)
    {
        throw new RuntimeException( "unimplemented" );
    }

    @Override
    public AlgebraicNumber dividedBy(AlgebraicNumber that)
    {
        throw new RuntimeException( "unimplemented" );
    }

    @Override
    public boolean isRational()
    {
        throw new RuntimeException( "unimplemented" );
    }

    @Override
    public int signum()
    {
        throw new RuntimeException( "unimplemented" );
    }

    @Override
    public AlgebraicNumber negate()
    {
        throw new RuntimeException( "unimplemented" );
    }

    @Override
    public AlgebraicNumber reciprocal()
    {
        throw new RuntimeException( "unimplemented" );
    }

    @Override
    public void getNumberExpression(StringBuffer buf, int format)
    {
        throw new RuntimeException( "unimplemented" );
    }

    @Override
    public String toString(int format)
    {
        throw new RuntimeException( "unimplemented" );
    }
}
