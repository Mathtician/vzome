
//(c) Copyright 2010, Scott Vorthmann.

package com.vzome.core.editor;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.vzome.core.commands.XmlSaveFormat;
import com.vzome.core.commands.Command.Failure;

public class NoOp implements UndoableEdit
{
    @Override
    public void perform() throws Failure
    {
        throw new IllegalStateException();
    }

    @Override
    public void undo()
    {
        throw new IllegalStateException();
    }

    @Override
    public void redo() throws Failure
    {
        throw new IllegalStateException();
    }

    @Override
    public boolean isVisible()
    {
        return false;
    }

    @Override
    public boolean isDestructive()
    {
        return false;
    }

    @Override
    public Element getXml( Document doc )
    {
        throw new IllegalStateException();
    }

    @Override
    public Element getDetailXml( Document doc )
    {
        return getXml( doc );
    }

    @Override
    public void loadAndPerform( Element xml, XmlSaveFormat format,
            Context context ) throws Failure
    {
        throw new IllegalStateException();
    }

    @Override
    public boolean isSticky()
    {
        return false;
    }
}
