
package com.vzome.core.model;

import com.vzome.core.render.Color;


public interface ManifestationChanges
{
    void manifestationAdded( Manifestation m );
    
    void manifestationRemoved( Manifestation m );
    
    void manifestationColored( Manifestation m, Color color );
}
