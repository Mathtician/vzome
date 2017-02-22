package com.vzome.core.math;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import com.vzome.core.algebra.AlgebraicField;
import com.vzome.core.algebra.AlgebraicNumber;
import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.math.symmetry.Direction;


public class Polyhedron
{
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((length == null) ? 0 : length.hashCode());
		result = prime * result + ((m_faces == null) ? 0 : m_faces.hashCode());
		result = prime * result
				+ ((m_vertexList == null) ? 0 : m_vertexList.hashCode());
		result = prime * result
				+ ((m_vertices == null) ? 0 : m_vertices.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + numVertices;
		result = prime * result + ((orbit == null) ? 0 : orbit.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		Polyhedron other = (Polyhedron) obj;
		if (length == null) {
			if (other.length != null) {
				return false;
			}
		} else if (!length.equals(other.length)) {
			return false;
		}
		if (m_faces == null) {
			if (other.m_faces != null) {
				return false;
			}
		} else if (!m_faces.equals(other.m_faces)) {
			return false;
		}
		if (m_vertexList == null) {
			if (other.m_vertexList != null) {
				return false;
			}
		} else if (!m_vertexList.equals(other.m_vertexList)) {
			return false;
		}
		if (m_vertices == null) {
			if (other.m_vertices != null) {
				return false;
			}
		} else if (!m_vertices.equals(other.m_vertices)) {
			return false;
		}
		if (name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!name.equals(other.name)) {
			return false;
		}
		if (numVertices != other.numVertices) {
			return false;
		}
		if (orbit == null) {
			if (other.orbit != null) {
				return false;
			}
		} else if (!orbit.equals(other.orbit)) {
			return false;
		}
		return true;
	}

	private static Logger logger = Logger .getLogger( "com.vzome.core.math.Polyhedron" );
    
	protected int numVertices = 0;

	protected final Map<AlgebraicVector, Integer> m_vertices = new HashMap<>();
    
	protected final List<AlgebraicVector> m_vertexList = new ArrayList<>();

	protected final Set<Face> m_faces = new HashSet<>();
    
    private final AlgebraicField field;

	public Polyhedron( AlgebraicField field )
    {
        this.field = field;
    }
    
    public AlgebraicField getField()
    {
        return field;
    }
    
    private String name;

    private Direction orbit;

    private AlgebraicNumber length;
    
    public void setName( String name )
    {
        this.name = name;
    }
    
    public String getName()
    {
        return name;
    }

	public void addVertex( AlgebraicVector location ) throws Error
	{
	    m_vertexList .add( location );
	}

	/**
	 * Only used in ZomicPolyhedronModelInterpreter.
	 * This used to be the implementation of addVertex, but all other callers
	 * don't use the return value, and have already assigned their own indices,
	 * so the collisions here are a bad idea.
	 * @param halfLoc
	 * @return
	 */
    public Integer addIndexedVertex( AlgebraicVector location )
    {
        Integer vertexObj = m_vertices.get( location );
        if ( vertexObj == null ) {
            m_vertexList .add( location );
            // IMPORTANT: the incremented value is not returned
            m_vertices .put( location, vertexObj = numVertices++ );
        }
        return vertexObj;
    }

	public void addFace( Face face )
	{
        face .computeNormal( m_vertexList );
		face .canonicallyOrder(); // so the contains comparison works
		if ( ! m_faces .contains( face ) ) {
			m_faces .add( face );
			
//			System .out .println( "--------face" );
//			for ( Iterator<Integer> vertices = face .iterator(); vertices .hasNext(); )
//			    System .out .println( m_vertexList .get(vertices.next() ) );
		}
	}
	
	public List<AlgebraicVector> getVertexList(){
		return m_vertexList;
	}

	public Set<Face> getFaceSet(){
		return m_faces;
	}


    public Face newFace()
    {
        return new Face();
    }
	

	public class Face extends ArrayList<Integer>
    {
        private AlgebraicVector mNormal;
        
        private Face(){}
		
		public int getVertex( int index )
        {
            if ( index >= size() )
            {
                String msg = "index larger than Face size";
                logger .severe( msg );
                throw new IllegalStateException( msg );
            }
			return get( index );
		}
        
        public void computeNormal( List<AlgebraicVector> vertices )
        {
            AlgebraicVector v0 = vertices .get( getVertex( 0 ) );
            AlgebraicVector v1 = vertices .get( getVertex( 1 ) );
            AlgebraicVector v2 = vertices .get( getVertex( 2 ) );
            v1 = v1 .minus( v0 );
            v2 = v2 .minus( v0 );
            mNormal = v1 .cross( v2 );
        }
		
		public void canonicallyOrder()
		{
			int minIndex = -1;
			int minVertex = Integer.MAX_VALUE;
			int sz = size();
			for ( int i = 0; i < sz; i++ )
				if ( getVertex(i) <= minVertex ){
					minVertex = getVertex(i);
					minIndex = i;
				}
			Integer[] temp = new Integer[ sz ];	
			for ( int j = 0; j < sz; j++  ) {
				temp[j] = get( (j+minIndex) % sz );
			}
			for ( int k = 0; k < sz; k++ )
				set( k, temp[k] );
		}
		
        @Override
		public int hashCode()
		{
			int tot = 0;
			for ( int i = 0; i < size(); i++ )
				tot += getVertex(i);
			return tot;
		}
		
        @Override
		public boolean equals( Object other )
		{
			if( other == null )
				return false;
			if ( other == this )
			    return true;
			if ( ! ( other instanceof Face ) )
				return false;
			Face otherFace = (Face) other;
			if ( otherFace .size() != size() )
			    return false;
			for ( int i = 0; i < size(); i++ )
				// this relies on both faces being in canonical order
				if ( ! get(i) .equals( otherFace .get(i)) )
					return false;
			return true;
		}
        
        public AlgebraicVector getNormal()
        {
            return mNormal;
        }
	}
	
    public void setOrbit( Direction orbit )
    {
        this .orbit = orbit;
    }

    public void setLength( AlgebraicNumber length )
    {
        this .length = length;
    }

    public Direction getOrbit()
    {
        return this .orbit;
    }

    public AlgebraicNumber getLength()
    {
        return this .length;
    }
}


