package org.brunosimoes.programs.paintbrush;

import java.applet.Applet;
import java.awt.Color;

/**
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.<br><br>
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.<br><br>
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 675 Mass Ave, Cambridge, MA 02139, USA.<br><br>
 *
 * @version 1.0.0.1 (20040301)
 * @author <a href="mailto:brunogsimoes@gmail.com">Bruno Simões</a>  
 *
 * Copyright (c) 2004 Bruno Simões. All Rights Reserved.
 */

public class PaintBrushApplet extends Applet {

	private static final long serialVersionUID = 873317986287453309L;

	public void init(){
		setBackground(new Color(170,170,170));
		new PaintBrush();
	}

}
