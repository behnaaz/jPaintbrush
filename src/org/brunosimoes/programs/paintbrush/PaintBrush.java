package org.brunosimoes.programs.paintbrush;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Label;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.Panel;
import java.awt.Point;
import java.awt.TextField;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.FileOutputStream;

import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JSlider;

import simoes.ui.about.About;

import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGImageEncoder;

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

public class PaintBrush extends JFrame implements Printable, ActionListener, MouseListener, MouseMotionListener, KeyListener {

	private static final long serialVersionUID = 7194240619983197658L;
	private static final String name = "PaintBrush";
	private static final String version = "1.0.0.1";
	private static final String released = "20040301";
	private static final String author = "Bruno Simões";

	Color corTela = Color.white;
	Color corLinha = Color.red;
	Panel fundo, cima, cima2, baixo, top;
	Image offscreenImage;
	Graphics tela;
	Graphics fundoGrafico;
	Dimension d;
	Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	TextField status;
	int x, y;
	String operacao = "free_hand";
  	JComboBox nrVertices = new JComboBox();

	/** Variaveis usadas para desenhar as curvas de Bezier */
	Point[] listaDeCoordenadas;

	/** Numero de pontos */
	int numPontos;

	/** Intervalo de tempo */
	double t;

	/** Tempo do passos para desenhar a curva */
	double k = .025;
	JSlider nrVSlider = new JSlider();
	JSlider corSlider = new JSlider();
	JSlider nrSSlider = new JSlider();
	Label nrv;
	Label fcor;
	Label nrs;

	/** Variaveis utilizadas para desenhar os poligonos */

	/** Distancia do ponto ao centro */
	double raio = 0;

	/** Angulo do centro ao ponto */
	double orientacao = 0;

	/** Coordenadas do centro */
	int poligono_x, poligono_y;

	/** Numero de pontos */
	int numeroVertices = 5;

	/** Pontos de intervalo */
	int pontosSaltados = 1;

	/** Frequencia com que as linhas mudam de cor */
	int factorDeCor = 4;

	public void buildUI(){

		/* Painel superior */
		cima2 = new Panel();
		cima2.setFont(new Font("Courier",Font.BOLD,15));
		cima2.add(new Label("   " + name + " "));
		cima2.setBackground(Color.green);

		/* Painel superior 2 */
		cima = new Panel();
		cima.setFont(new Font("Courier",Font.BOLD,12));
		cima.add(new Label("Vertices "));
		nrVSlider.setMajorTickSpacing(30);
		nrVSlider.setForeground(Color.lightGray);
		nrVSlider.setBackground(Color.green);
		nrv = new Label (""+numeroVertices);
		nrVSlider.addMouseListener(this);
		nrVSlider.addMouseMotionListener(this);
		nrVSlider.setValue(5);
		cima.add(nrVSlider);
		cima.add(nrv);

		cima.add(new Label("Saltos "));
		nrSSlider.setMajorTickSpacing(30);
		nrSSlider.setForeground(Color.lightGray);
		nrSSlider.setBackground(Color.green);
		nrs = new Label (""+pontosSaltados);
		nrSSlider.addMouseListener(this);
		nrSSlider.addMouseMotionListener(this);
		nrSSlider.setValue(1);
		cima.add(nrSSlider);
		cima.add(nrs);

		cima.add(new Label("F. Cor "));
		corSlider.setMajorTickSpacing(30);
		corSlider.setForeground(Color.lightGray);
		corSlider.setBackground(Color.green);
		corSlider.addMouseListener(this);
		corSlider.addMouseMotionListener(this);
		corSlider.setValue(4);
		fcor = new Label (""+factorDeCor);
		cima.add(corSlider);
		cima.add(fcor);

		cima.setBackground(Color.green);

		top = new Panel();
		top.setBackground (Color.green);
		top.setLayout (new BorderLayout () );
		top.add("North", cima2);
		top.add("Center", cima);
		cima.setVisible(false);

		/* Painel inferior */
		baixo = new Panel();
		status = new TextField ("                      ");
		baixo.add(status);
		status.setEditable(false);
		status.setForeground(Color.black);
		status.setBackground(Color.red);
		baixo.setBackground(Color.red);

		fundo = new Panel();
		fundo.addMouseListener( this );
		fundo.addMouseMotionListener( this );

		setTitle(name + " - Computacao Grafica - " + author); 
		setSize (screenSize.width-40, screenSize.height-80);
		setLocation(20, 20);
		setBackground (Color.white);

		setLayout (new BorderLayout () );
		add("North", top);
		add("Center", fundo);
		add("South", baixo);

		MenuBar myMenuBar = new MenuBar();
		this.criarMenuTangram(myMenuBar);
		this.criarMenuOpcoes(myMenuBar);
		this.criarMenuCenarios(myMenuBar);
		this.criarCreditos(myMenuBar);
		setMenuBar(myMenuBar);
		setVisible (true);
		setResizable(false);
		validate();	
	}

	public void setPolyPref(boolean visible){
		cima.setVisible(visible);
		repaint();
		validate();
	}

	public PaintBrush(){
		buildUI();
		d = fundo.getSize ();
		fundoGrafico = fundo.getGraphics ();
		offscreenImage = createImage ( d.width, d.height );
		tela = offscreenImage.getGraphics ();
		tela.setColor (corTela);
		tela.fillRect ( 0, 0, d.width, d.height );
		numPontos = 0;
		listaDeCoordenadas = new Point [4];
	}

	public void paint( Graphics g ) {
		if (operacao == "bezier" && numPontos > 0){
			tela.fillOval(listaDeCoordenadas[0].x-2, listaDeCoordenadas[0].y-2,4,4);

			/** Calcula e desenha a curva de Bezier */
			if(numPontos == 4) {
				double x1,x2,y1,y2;
				x1 = listaDeCoordenadas[0].x;
				y1 = listaDeCoordenadas[0].y;
				for(t=k;t<=1+k;t+=k){

					/** Usa os polinomios de Berstein */
					x2=(listaDeCoordenadas[0].x+t*(-listaDeCoordenadas[0].x*3+t*(3*listaDeCoordenadas[0].x-
					listaDeCoordenadas[0].x*t)))+t*(3*listaDeCoordenadas[1].x+t*(-6*listaDeCoordenadas[1].x+
					listaDeCoordenadas[1].x*3*t))+t*t*(listaDeCoordenadas[2].x*3-listaDeCoordenadas[2].x*3*t)+
					listaDeCoordenadas[3].x*t*t*t;

					y2=(listaDeCoordenadas[0].y+t*(-listaDeCoordenadas[0].y*3+t*(3*listaDeCoordenadas[0].y-
					listaDeCoordenadas[0].y*t)))+t*(3*listaDeCoordenadas[1].y+t*(-6*listaDeCoordenadas[1].y+
					listaDeCoordenadas[1].y*3*t))+t*t*(listaDeCoordenadas[2].y*3-listaDeCoordenadas[2].y*3*t)+
					listaDeCoordenadas[3].y*t*t*t;

					/** Desenha curva */
					tela.drawLine((int)x1,(int)y1,(int)x2,(int)y2);
					x1 = x2;
					y1 = y2;
				}
			tela.fillOval(listaDeCoordenadas[3].x-2, listaDeCoordenadas[3].y-2,4,4);
			numPontos = 0;
			status.setText("");
			}
		}
		fundoGrafico.drawImage ( offscreenImage, 0, 0, this );
	}

	protected void adicionarItem(Menu m, String s) {
		MenuItem mi = new MenuItem(s);
		mi.addActionListener(this);
		m.add(mi); 
	}

	protected void criarMenuTangram(MenuBar mb) {
		Menu menuTangram = new Menu("PaintBrush");
		this.adicionarItem(menuTangram, "Novo");
		this.adicionarItem(menuTangram, "Guardar");
		this.adicionarItem(menuTangram, "Imprimir");
		this.adicionarItem(menuTangram, "Sair");
		mb.add(menuTangram);
	}

	protected void criarMenuOpcoes(MenuBar mb) {
		Menu menuOpcoes = new Menu("Opcoes");
		this.adicionarItem(menuOpcoes, "Apagar");
		this.adicionarItem(menuOpcoes, "Cor da Linha");
		mb.add(menuOpcoes);
	} 

	protected void criarMenuCenarios(MenuBar mb) {
		Menu menuCenarios = new Menu("Modo");
		this.adicionarItem(menuCenarios, "Curvas de Bezier");
		//menuCenarios.addSeparator();
		this.adicionarItem(menuCenarios, "Free Hand");
		this.adicionarItem(menuCenarios, "Criar Poligono");
		mb.add(menuCenarios);
	}

	protected void criarCreditos(MenuBar mb) {
		Menu menuCreditos = new Menu("Creditos");
		this.adicionarItem(menuCreditos, "Ver Creditos");
		mb.add(menuCreditos);
	} 

	public void actionPerformed( ActionEvent e ) {
		String opcao = e.getActionCommand();
		if (opcao == "Ver Creditos"){
			About.setInformation(name, version, released);
			new About();
		}
		if (opcao == "Criar Poligono"){
			try {
				operacao = "criar_poligono"; 
				setPolyPref(true);
				status.setText("          Escolha dois pontos");
				poligono_x = 0;
				poligono_y = 0;
				fundo.validate();
				fundo.repaint();
				}
			catch (Exception er){}
		}
		if (opcao == "Free Hand") { 
			setPolyPref(false);
			status.setText("");
			operacao = "free_hand"; 
			x=0; 
			y=0; 
		}
		if (opcao == "Curvas de Bezier") { 
			setPolyPref(false);
			status.setText("");
			operacao = "bezier"; 
			x=0;
			y=0;
		}
		if (opcao == "Guardar") {
			directorio();
			numPontos = 0;
		}
		if (opcao == "Imprimir") {
			PrinterJob printerJob = PrinterJob.getPrinterJob();
			PageFormat pageFormat = printerJob.defaultPage();
			if(!printerJob.printDialog()) return;
			pageFormat = printerJob.pageDialog(pageFormat);
			printerJob.setPrintable(this, pageFormat);
			try { printerJob.print(); }
			catch (PrinterException erro) { System.out.println(e.toString());}
		}
		if (opcao == "Apagar" || opcao == "Novo")	apagar();
		if (opcao == "Cor da Linha") corLinha = JColorChooser.showDialog(this, "Background Color", corLinha);
		if (opcao == "Sair") terminar();
		paint(tela);
	}

	public void mouseClicked(MouseEvent e) {}
	public void mousePressed(MouseEvent e) {
		try {
			x = e.getX();
			y = e.getY();
			tela.setColor(corLinha);

			if (operacao == "criar_poligono"){
				numeroVertices = nrVSlider.getValue();
				factorDeCor = corSlider.getValue();
				pontosSaltados = nrSSlider.getValue();
				nrs.setText(""+pontosSaltados);
				nrv.setText(""+numeroVertices);
				fcor.setText(""+factorDeCor);
				cima.repaint();
				if (e.getY()>16){
					if (poligono_x != 0 && poligono_y != 0){
						criarPoligono();
						status.setText("          Escolha dois pontos");
					}
					else {
						poligono_x = x;
						poligono_y = y;
						status.setText("        Escolha mais um ponto");
					}
				}
			}

			if (operacao == "free_hand") tela.drawLine(x,y,x,y);
			
			if (operacao == "bezier"){
				// Se existirem menos de 4 adiciona outro
				if(numPontos < 4) {
					listaDeCoordenadas[numPontos] = new Point(x,y);
					numPontos++;
				}
				status.setText("          Pontos guardados: "+numPontos);
			}
		paint(tela);
		}
		catch (Exception erro){}
	}
	public void mouseReleased(MouseEvent e) {}
	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}
	public void mouseMoved(MouseEvent e) { 
		try { paint(tela); }
		catch (Exception er) {}
	}
	public void mouseDragged(MouseEvent e) {
		try {
			int x0 = e.getX();
			int y0 = e.getY();

			x = (x == 0)?x0:x;
			y = (y == 0)?y0:y;

			if (operacao == "criar_poligono"){
				numeroVertices = nrVSlider.getValue();
				factorDeCor = corSlider.getValue();
				pontosSaltados = nrSSlider.getValue();
				nrs.setText(""+pontosSaltados);
				nrv.setText(""+numeroVertices);
				fcor.setText(""+factorDeCor);
				cima.repaint();
			}	

			if (operacao == "free_hand"){
				tela.setColor(corLinha);
				tela.drawLine(x,y,x0,y0);
			}
			x = x0; y=y0;
			paint(tela);
		} catch (Exception er){}
	}
	public void keyPressed( KeyEvent e ) {	}
	public void keyReleased( KeyEvent e ) {}
	public void keyTyped( KeyEvent e ) {}

	public void directorio() {
		FileDialog janela = new FileDialog(this,"Guarda imagem",FileDialog.SAVE);
		janela.setFile("imagem.jpg");
		janela.setVisible(true);
		String directorio, nome;
		try { 
			directorio = janela.getDirectory(); 
			nome = janela.getFile();

			/* Validar o nome */
			int i = 0;
			int tamanho = nome.length();
			while (i < tamanho && nome.charAt(i) != '.'){
				directorio += nome.charAt(i);
				i++;
			}
			directorio += ".jpg";
			guardar_em_JPG (directorio);
		}
		catch(Exception exc){}
	}

	public void guardar_em_JPG(String directorio){
		BufferedImage imagem = (BufferedImage) offscreenImage;
		try {
			FileOutputStream outFile = new FileOutputStream(directorio);
			JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(outFile);
			encoder.encode(imagem);
			outFile.close();
			System.out.println("Imagem guardada com sucesso em "+directorio);
		} 
		catch (Exception e) {	System.out.println(e.toString());}
	}

	public void apagar (){
		tela.setColor (Color.white);
		tela.fillRect ( 0, 0, d.width, d.height );
		poligono_x = poligono_y = x = y = 0;
		numPontos = 0;
		paint (tela);
	}

	public int print(Graphics g, PageFormat pageFormat, int pageIndex) {
		if (pageIndex > 0) return NO_SUCH_PAGE;
	
		fundoGrafico.drawImage ( offscreenImage, 0, 0, this );
		return PAGE_EXISTS;
	}	

	public void criarPoligono(){
		int dx = Math.abs(poligono_x-x);
		dx = dx*dx;
		int dy = Math.abs(poligono_y-y);
		dy = dy*dy;
		raio = Math.sqrt(dx+dy);
		double angulo = orientacao;
		double anguloIncrement = 360*((double)pontosSaltados/(double)numeroVertices);
		int oldX = poligono_x+(int)Math.round(raio*Math.cos(angulo/180.0*Math.PI));
		int oldY = poligono_y+(int)Math.round(raio*Math.sin(angulo/180.0*Math.PI));
		int newX;
		int newY;
		int colorPosition = 0;
		for (int i = 1; i <= numeroVertices; i++) {
			angulo += anguloIncrement;
			newX = poligono_x+(int)Math.round(raio*Math.cos(angulo/180.0*Math.PI));
			newY = poligono_y+(int)Math.round(raio*Math.sin(angulo/180.0*Math.PI));
			tela.setColor(new Color(Color.HSBtoRGB((float)colorPosition/(float)numeroVertices,1.0f,1.0f)));
			tela.fillOval(oldX-6, oldY-6, 13, 13);
			tela.drawLine(oldX, oldY, newX, newY);
			oldX = newX;
			oldY = newY;
			colorPosition += factorDeCor;
			while (colorPosition >= numeroVertices)
			colorPosition -= numeroVertices;
		}
		poligono_x = poligono_y = 0;
	}

	public void terminar() {
		try {
			setVisible(false);
			dispose();
 		}
		catch (Exception e){System.out.println("Erro ao terminar o programa.");}
	}

	public static void main (String args []){
		new PaintBrush();
	}
}