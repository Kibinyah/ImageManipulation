/*
CS-255 Getting started code for the assignment
I do not give you permission to post this code online
Do not post your solution online
Do not copy code
Do not use JavaFX functions or other libraries to do the main parts of the assignment:
	Gamma Correction
	Contrast Stretching
	Histogram calculation and equalisation
	Cross correlation
All of those functions must be written by yourself
You may use libraries to achieve a better GUI
*/
//Kevin Pan 969449
//This code is all my own work.
import java.io.FileInputStream; 
import java.io.FileNotFoundException;
import java.util.ArrayList;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.chart.Axis;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.FlowPane;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;  
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.PixelReader;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class Photoshop extends Application {

	double gammaVal;
	
	double r1 = 50;
	double r2 = 200;
	double s1 = 20;
	double s2 = 225;
	int counter = 2;
	
	
	int[] redHistogram = new int[256];
	int[] greenHistogram = new int[256];
	int[] blueHistogram = new int[256];
	int[] greyHistogram = new int[256];
	int[] equalisedHistogram = new int[256];
	int[] mapping = new int[288471];
	

    int[] intensityGrey = new int [288471];
    
    boolean temp = false;
	
	
    @Override
    public void start(Stage stage) throws FileNotFoundException {
		stage.setTitle("Photoshop");

		//Read the image
		Image image = new Image(new FileInputStream("raytrace.jpg"));  

		//Create the graphical view of the image
		ImageView imageView = new ImageView(image); 
		
		//Create the simple GUI
		Button invert_button = new Button("Invert");
		Button gamma_button = new Button("Gamma Correct");
		Button contrast_button = new Button("Contrast Stretching");
		Button histogram_button = new Button("Histograms");
		Button cc_button = new Button("Cross Correlation");

	    
	    Slider slider = new Slider();
	    slider.setMin(0);
	    slider.setMax(5.0);
	    slider.setValue(2.8);
	    slider.setShowTickLabels(true);
	    slider.setShowTickMarks(true);
	    slider.setMajorTickUnit(2.5);
	    slider.setMinorTickCount(1);
	    
	   Canvas contrastCanvas = new Canvas(255,255);
	   GraphicsContext gc = contrastCanvas.getGraphicsContext2D();
	   gc.setFill(Color.CYAN);
	   gc.fillRect(0, 0, 255, 255);	 
	   gc.strokeLine(0,255,255,0);
	   gc.setLineWidth(1);
	   
	  
	   
	   contrastCanvas.setOnMouseClicked(new EventHandler<MouseEvent>() {
		   @Override
		   public void handle(MouseEvent event) {
			  
			  if(counter == 2) {
				  counter = counter - 1;
				  
				  
				  r1 = event.getX();
				  s1 = event.getY();
				
				  gc.fillRect(0, 0, 255, 255);
				  gc.strokeLine(0 ,255, r1, s1);
				  gc.strokeLine(r1,s1,255,0);
				  
				  
			  
			  }else if (counter == 1) {
				  counter = counter - 1;
				  r2 = event.getX();
				  s2 = event.getY();
				  
				  gc.fillRect(0, 0, 255, 255);
				  gc.strokeLine(0 ,255, r1, s1);
				  gc.strokeLine(r1, s1, r2, s2);
				  gc.strokeLine(r2,s2,255,0);
			  } else {
				  
				  double distance1 =  Math.sqrt(Math.pow(event.getX()-r1,2) + Math.pow(event.getY()-(s1),2));
				  double distance2 =  Math.sqrt(Math.pow(event.getX()-r2,2) + Math.pow(event.getY()-(s2),2));
				  	if(distance1 <= distance2) {

				  		r1 = event.getX();
						s1 = event.getY();
						gc.fillRect(0, 0, 255, 255);
						gc.strokeLine(0 ,255, r1, s1);
						gc.strokeLine(r1, s1, r2, s2);
						gc.strokeLine(r2,s2,255,0);
				  		
				  	}else {
				  		r2 = event.getX();
						s2 = event.getY();
						gc.fillRect(0, 0, 255, 255);
						gc.strokeLine(0 ,255, r1, s1);
						gc.strokeLine(r1, s1, r2, s2);
						gc.strokeLine(r2,s2,255,0);
				  	}
			  }		  
	   
		   }
		 })
	   ;
	   
	   NumberAxis xRedAxis = new NumberAxis();
	   NumberAxis yRedAxis = new NumberAxis();
	   xRedAxis.setLabel("Red Histogram");
	   yRedAxis.setLabel("Intensity");
	   
	   LineChart<Number,Number> redLineChart = new LineChart<Number,Number>(xRedAxis,yRedAxis);
	   redLineChart.setCreateSymbols(false);
	   
	   NumberAxis xGreenAxis = new NumberAxis();
	   NumberAxis yGreenAxis = new NumberAxis();
	   xGreenAxis.setLabel("Green Histogram");
	   yGreenAxis.setLabel("Intensity");
	   
	   LineChart<Number,Number> greenLineChart = new LineChart<Number,Number>(xGreenAxis,yGreenAxis);
	   greenLineChart.setCreateSymbols(false);
	   
	   NumberAxis xBlueAxis = new NumberAxis();
	   NumberAxis yBlueAxis = new NumberAxis();
	   xBlueAxis.setLabel("Blue Histogram");
	   yBlueAxis.setLabel("Intensity");
	   
	   LineChart<Number,Number> blueLineChart = new LineChart<Number,Number>(xBlueAxis,yBlueAxis);
	   blueLineChart.setCreateSymbols(false);
	   
	   NumberAxis xGreyAxis = new NumberAxis();
	   NumberAxis yGreyAxis = new NumberAxis();
	   xGreyAxis.setLabel("Brightness Histogram");
	   yGreyAxis.setLabel("Intensity");
	   
	   LineChart<Number,Number> greyLineChart = new LineChart<Number,Number>(xGreyAxis,yGreyAxis);
	   greyLineChart.setCreateSymbols(false);
	   
	   NumberAxis xEqualiseAxis = new NumberAxis();
	   NumberAxis yEqualiseAxis = new NumberAxis();
	   xEqualiseAxis.setLabel("Equalised Brightness Histogram");
	   yEqualiseAxis.setLabel("Intensity");
	   
	   LineChart<Number,Number> equaliseChart = new LineChart<Number,Number>(xEqualiseAxis,yEqualiseAxis);
	   equaliseChart.setCreateSymbols(false);
	   
	   
	   
		//Add all the event handlers (this is a minimal GUI - you may try to do better)
		invert_button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                System.out.println("Invert");
				//At this point, "image" will be the original image
				//imageView is the graphical representation of an image
				//imageView.getImage() is the currently displayed image
				
				//Let's invert the currently displayed image by calling the invert function later in the code
				Image inverted_image=ImageInverter(imageView.getImage());
				//Update the GUI so the new image is displayed
				imageView.setImage(inverted_image);
				



            }
        });

		gamma_button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                System.out.println("Gamma Correction");
                gammaVal = slider.getValue();
                
                Image gamma_image=GammaCorrection(imageView.getImage());
				//Update the GUI so the new image is displayed
				imageView.setImage(gamma_image);
            }
        });

		contrast_button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                System.out.println("Contrast Stretching");
                Image contrast_image = ContrastStretching(imageView.getImage());
                imageView.setImage(contrast_image);
            }
        });
		
		histogram_button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                System.out.println("Histogram");
                Image readhisto_image = ReadHistogram(imageView.getImage());
                imageView.setImage(readhisto_image);
                
                XYChart.Series<Number,Number> redData = new XYChart.Series<>();
                XYChart.Series<Number,Number> blueData = new XYChart.Series<>();
                XYChart.Series<Number,Number> greenData = new XYChart.Series<>();
                XYChart.Series<Number,Number> greyData = new XYChart.Series<>();
                XYChart.Series<Number,Number> equalisedData = new XYChart.Series<>();
              
                for (int i = 0; i < 256; i++) {
                	
                	
                	
                //	System.out.println(intensity);
                //	mapping[i] = 255*(redhistogram[i]/intensity);
                	redData.getData().add(new XYChart.Data<Number,Number>(i,redHistogram[i]));
                	blueData.getData().add(new XYChart.Data<Number,Number>(i,blueHistogram[i]));
                	greenData.getData().add(new XYChart.Data<Number,Number>(i,greenHistogram[i]));
                	greyData.getData().add(new XYChart.Data<Number,Number>(i,greyHistogram[i]));
                	
                }
                redLineChart.getData().add(redData);
                blueLineChart.getData().add(blueData);
                greenLineChart.getData().add(greenData);
                greyLineChart.getData().add(greyData);

                
                Image histo_image = Histogram(imageView.getImage());
                imageView.setImage(histo_image);
                
                for(int i = 0; i<256;i++) {
                	equalisedData.getData().add(new XYChart.Data<Number,Number>(i,equalisedHistogram[i]));
                }
                equaliseChart.getData().add(equalisedData);
            }
        });
		
		cc_button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                System.out.println("Cross Correlation");
                Image cc_image = CrossCorrelation(imageView.getImage());
                imageView.setImage(cc_image);
            }
        });
		
		//Using a flow pane
		FlowPane root = new FlowPane();
		//Gaps between buttons
		root.setVgap(10);
        root.setHgap(5);

		//Add all the buttons and the image for the GUI
		root.getChildren().addAll(invert_button,slider,gamma_button, contrast_button, histogram_button, cc_button, imageView,contrastCanvas,redLineChart, greenLineChart, blueLineChart, greyLineChart,equaliseChart );
		
		ScrollPane sp = new ScrollPane();
		sp.setContent(root);

		//Display to user
        Scene scene = new Scene(sp, 1024, 768);
        stage.setScene(scene);
        stage.show();
    }

	//Example function of invert
	public Image ImageInverter(Image image) {
		//Find the width and height of the image to be process
		int width = (int)image.getWidth();
        int height = (int)image.getHeight();
		//Create a new image of that width and height
		WritableImage inverted_image = new WritableImage(width, height);
		//Get an interface to write to that image memory
		PixelWriter inverted_image_writer = inverted_image.getPixelWriter();
		//Get an interface to read from the original image passed as the parameter to the function
		PixelReader image_reader=image.getPixelReader();
		
		//Iterate over all pixels
		for(int y = 0; y < height; y++) {
			for(int x = 0; x < width; x++) {
				//For each pixel, get the colour
				Color color = image_reader.getColor(x, y);
				//the getColor function returns colours as 0..1 doubles (multiply by 255 if to get 0-255 colours)
				color=Color.color(1.0-color.getRed(), 1.0-color.getGreen(), 1.0-color.getBlue());
				//Note: for gamma correction, may not need the divide by 255 since getColor already returns 0-1, nor may need multiply by 255 since the Color.color function consumes 0-1 doubles.
				
				//Apply the new colour
				inverted_image_writer.setColor(x, y, color);
			}
		}
		return inverted_image;
	}
	
	//Gamma correction makes the image uniformly lighter or darker depending on the gamma value.
	//Equation = (I/a)^1/gammaval
	public Image GammaCorrection(Image image) {
		
		ArrayList<Double> lookupTable = new ArrayList<Double>();
	
		int width = (int)image.getWidth();
		int height = (int)image.getHeight();
		WritableImage gamma_image = new WritableImage(width,height);
		PixelWriter gamma_image_writer = gamma_image.getPixelWriter();
		PixelReader image_reader = image.getPixelReader();
		
		//lookup table runs the gamma equation from i = 0  to i = 255 and adds the result to the arraylist.
		for(int i = 0; i < 256; i++)
		{
			lookupTable.add(Math.pow(i/255.0, 1/gammaVal));
		}
		
		for(int y = 0; y < height; y++) {
			for(int x = 0; x < width; x++) {
				//For each pixel, get the colour
				Color color = image_reader.getColor(x, y);
			
				//Gets red, green and blue values of the pixel and multiply by 255 to get the value between 0 to 255
				int red = (int) (color.getRed()*255);
				int green = (int) (color.getGreen()*255);
				int blue = (int) (color.getBlue()*255);
				
				//Changes the colour of the pixel to new values by searching the index of the RGB values in the lookupTable which returns the new result.
				color= Color.color(lookupTable.get(red), lookupTable.get(green), lookupTable.get(blue));
				
				//Applys the new colour
				gamma_image_writer.setColor(x, y, color);
			}
		}
		return gamma_image;
	}
	
	//Contrast stretching allows certain colours to be brighter or darker by increasing the dynamic range of an image.
	//Gradients that are smaller than the normal gradient have less change for the range of colours it covers.
	//Gradients that are larger than the normal gradient have more change for the range of colours it covers.
	public Image ContrastStretching(Image image) {
		
		ArrayList<Double> lookupTable = new ArrayList<Double>();
		
		int width = (int)image.getWidth();
		int height = (int)image.getHeight();
		WritableImage contrast_image = new WritableImage(width,height);
		PixelWriter contrast_image_writer = contrast_image.getPixelWriter();
		PixelReader image_reader = image.getPixelReader();
		
		//lookup table runs the contrast equation from i = 0 to i = 255 and adds the result to the arraylist.
	    for(int i = 0; i < 256; i++)
	    {
	    	//As the origin point is the top left of the canvas not the bottom left, s1 and s2 would need to be subtracted from 255 to simulate the value from bottom to top.
			if(i < r1) {
				lookupTable.add((((255-s1) / r1) * (i))/255.0);
			} else if (r1 <= i && i <= r2) {
				lookupTable.add((((((255-s2)-(255-s1))/(r2-r1)) * (i-r1)) + (255-s1))/255.0);
			} else {
				lookupTable.add((((s2)/(255-r2) * (i-r2)) + (255-s2))/255.0);
			}
		}
		
		for(int y = 0; y < height; y++) {
			for(int x = 0; x < width; x++) {
				//For each pixel, get the colour
				Color color = image_reader.getColor(x, y);
				//Do something (in this case invert) - the getColor function returns colours as 0..1 doubles (we could multiply by 255 if we want 0-255 colours)
				
				//Gets red, green and blue values of the pixel and multiply by 255 to get the value between 0 to 255
				int red = (int) (color.getRed()*255);
				int green = (int) (color.getGreen()*255);
				int blue = (int) (color.getBlue()*255);
				
				//Changes the colour of the pixel to new values by searching the index of the RGB values in the lookupTable which returns the new result.
				color= Color.color(lookupTable.get(red), lookupTable.get(green), lookupTable.get(blue));
				
				//Apply the new colour
				contrast_image_writer.setColor(x, y, color);
			}
		}
	return contrast_image;
	}
	
	//Histogram equalisation makes the spread of intensity of pixels evenly.
	//
	public Image Histogram(Image image) {
		
		int newIntensity = 0;
		int width = (int)image.getWidth();
		int height = (int)image.getHeight();
		WritableImage histogram_image = new WritableImage(width,height);
		PixelWriter histogram_image_writer = histogram_image.getPixelWriter();
		PixelReader image_reader = image.getPixelReader();

		for(int y = 0; y < height; y++) {
			for(int x = 0; x < width; x++) {
				Color color = image_reader.getColor(x, y);
				
				
				int red = (int) (color.getRed()*255);
				//System.out.println(red);
				int green = (int) (color.getGreen()*255);
				//System.out.println(green);
				int blue = (int) (color.getBlue()*255);
				//System.out.println(blue);
				int grey = (red+blue+green)/ 3;
				//System.out.println(grey);
				
				newIntensity = mapping[grey];
				
				red = newIntensity;
				green = newIntensity;
				blue = newIntensity;
				
				equalisedHistogram[newIntensity]++;
				//System.out.println(equalisedHistogram[newIntensity]);
				
				color = Color.color(red/255.0, green/255.0, blue/ 255.0);
				histogram_image_writer.setColor(x, y, color);
			}
		
		}
	return histogram_image;
	}
	public Image ReadHistogram(Image image) {
		
		int width = (int)image.getWidth();
		int height = (int)image.getHeight();
		//WritableImage histogram_image = new WritableImage(width,height);
		//PixelWriter histogram_image_writer = histogram_image.getPixelWriter();
		PixelReader image_reader = image.getPixelReader();
		
		
		for (int i = 0; i < 256; i++) {
			redHistogram[i] = 0;
			greenHistogram[i] = 0;
			blueHistogram[i]= 0;
			greyHistogram[i] = 0;
			equalisedHistogram[i] = 0;
		}
		
		for(int y = 0; y < height; y++) {
			for(int x = 0; x < width; x++) {
				Color color = image_reader.getColor(x, y);
				
				int red = (int) (color.getRed()*255);
				int green = (int) (color.getGreen()*255);
				int blue = (int) (color.getBlue()*255);
				int grey = (red+blue+green) / 3;
				redHistogram[red]++;
				greenHistogram[green]++;
				blueHistogram[blue]++;
				greyHistogram[grey]++ ;
			
				
			}
		}
		//mapping = (grey culumative freq / size of image) * 255.0
		for(int i = 0; i < 256; i++) {
			if(i == 0) {	
		          intensityGrey[0] = greyHistogram[0];
	
		          mapping[0] = (int) (255.0 * (intensityGrey[0]/288470.0));
		      
		  	} else {
		      	intensityGrey[i] = intensityGrey[i-1] + greyHistogram[i];
		          
		      	mapping[i] = (int) ((intensityGrey[i]/288470.0) * 255.0);
		    
		  	}
		}
		
	return image;
	}
	
	//Cross Correlation creates an image that can remove noise or detect edges.
	// algorithm is the normalisation.
	//high pass filters can sharpen images.
	//low pass filters makes more blur on the image the bigger the filter is.
	//laplacian filter are used to find edges in images and are very sensitive to noise.
	public Image CrossCorrelation(Image image) {
		
	
		int[][] redFilter = new int[5][5];
		int[][] blueFilter = new int[5][5];
		int[][] greenFilter = new int [5][5];
		int[][] filter = new int [5][5];
		filter[0][0] = -4;
		filter[1][0] = -1;
		filter[2][0] = 0;
		filter[3][0] = -1;
		filter[4][0] = -4;
		filter[0][1] = -1;
		filter[1][1] = 2;
		filter[2][1] = 3;
		filter[3][1] = 2;
		filter[4][1] = -1;
		filter[0][2] = 0;
		filter[1][2] = 3;
		filter[2][2] = 4;
		filter[3][2] = 3;
		filter[4][2] = 0;
		filter[0][3] = -1;
		filter[1][3] = 2;
		filter[2][3] = 3;
		filter[3][3] = 2;
		filter[4][3] = -1;
		filter[0][4] = -4;
		filter[1][4] = -1;
		filter[2][4] =0;
		filter[3][4] = -1;
		filter[4][4] = -4;
		Color[] colour = new Color[25];
		int[] red = new int[25];
		int[] green = new int[25];
		int[] blue = new int[25];
		//int[] grey = new int[9];
		
		int max = 0;
		int min = 0;
		
		int redMax = 0;
		int blueMax = 0;
		int greenMax = 0;
		
		int redMin = 0;
		int blueMin = 0;
		int greenMin = 0;
		
		int width = (int)image.getWidth();
		int height = (int)image.getHeight();
		WritableImage cc_image = new WritableImage(width,height);
		PixelWriter cc_image_writer = cc_image.getPixelWriter();
		PixelReader image_reader = image.getPixelReader();
		
		Integer[][] redSum = new Integer[width][height];
		Integer[][] blueSum = new Integer[width][height];
		Integer[][] greenSum = new Integer[width][height];
		
		
		for(int i = 0; i < height; i++) {
			for(int j = 0;j<width;j++) {
			
					redSum[j][i] = null;
					blueSum[j][i] = null;
					greenSum[j][i] = null;
				
			}
		}
		
	
		
		for(int y = 2; y < height-2; y++) {
			for(int x = 2; x < width-2; x++) {
				
		
					colour[0] = image_reader.getColor(x-2, y-2);
					colour[1] = image_reader.getColor(x-1, y-2);
					colour[2] = image_reader.getColor(x, y-2);
					colour[3] = image_reader.getColor(x+1, y-2);
					colour[4] = image_reader.getColor(x+2, y-2);
					
					colour[5] = image_reader.getColor(x-2, y-1);
					colour[6] = image_reader.getColor(x-1, y-1);
					colour[7] = image_reader.getColor(x, y-1);
					colour[8] = image_reader.getColor(x+1, y-1);
					colour[9] = image_reader.getColor(x+2, y-1);

					colour[10] = image_reader.getColor(x-2, y);
					colour[11] = image_reader.getColor(x-1, y);
					colour[12] = image_reader.getColor(x, y);
					colour[13] = image_reader.getColor(x+1, y);
					colour[14] = image_reader.getColor(x+2, y);
										
					colour[15] = image_reader.getColor(x-2, y+1);
					colour[16] = image_reader.getColor(x-1, y+1);
					colour[17] = image_reader.getColor(x,  y+1);
					colour[18] = image_reader.getColor(x+1, y+1);
					colour[19] = image_reader.getColor(x+2, y+1);
					
					colour[20] = image_reader.getColor(x-2, y+2);
					colour[21] = image_reader.getColor(x-1, y+2);
					colour[22] = image_reader.getColor(x, y+2);
					colour[23] = image_reader.getColor(x+1, y+2);
					colour[24] = image_reader.getColor(x+2, y+2);
		
					
					for(int i = 0; i < 25; i++) {
							red[i] = (int) (colour[i].getRed()*255);
							green[i] = (int) (colour[i].getGreen()*255);
							blue[i] = (int) (colour[i].getBlue()*255);
							
					}
					
					int counter = 0;
					for(int i = 0; i < 5; i++) {
						for(int j = 0; j < 5; j++) {
							redFilter[j][i] = filter[j][i] * red[counter];
							blueFilter[j][i] = filter[j][i] * blue[counter];
							greenFilter[j][i] = filter[j][i] * green[counter];
							counter +=1;
							
						}
					}
					
					
					for(int i = 2; i < height-2; i++) {
						for(int j = 2; j < width-2; j++) {
						
							
								if(redSum[j][i] == null) {
									redSum[j][i] = 0;
									blueSum[j][i] = 0;
									greenSum[j][i] = 0;
									for(int p = 0; p < 5; p++) {
										for(int q = 0; q < 5; q++) {
											redSum[j][i] += redFilter[q][p]; 
											blueSum[j][i] += blueFilter[q][p];
											greenSum[j][i] += greenFilter[q][p];
											
										}
									}
									
									
									temp = true;
									break;
								}
								
							}
					
						if(temp == true) {
							temp = false;
							break;
						}
					}				
			}
		}
		
		
		
		for(int i = 2; i < height-2; i++) {
			for(int j = 2; j < width-2; j++) {
				if (redMax == 0)
					{
						redMax = redSum[j][i];
						blueMax = blueSum[j][i];
						greenMax = greenSum[j][i];
						
						redMin = redSum[j][i];
						blueMin = blueSum[j][i];
						greenMin = greenSum[j][i];
					}
				else {
						if(redSum[j][i] > redMax) {
							redMax = redSum[j][i];
						}
						
						if(blueSum[j][i] > blueMax) {
							blueMax = blueSum[j][i];
						}
						
						if(greenSum[j][i] > greenMax) {
							greenMax = greenSum[j][i];
						}
						
						if(redSum[j][i] < redMin) {
							redMin = redSum[j][i];
						}
						
						if(blueSum[j][i] < blueMin) {
							blueMin = blueSum[j][i];
						}
						
						if(greenSum[j][i] < greenMin) {
							greenMin = greenSum[j][i];
						}						
					}
			}
		}
		
		System.out.println(blueMax);
		System.out.println(redMax);
		System.out.println(greenMax);
		System.out.println(blueMin);
		System.out.println(redMin);
		System.out.println(greenMin);
		
		if(redMax >= blueMax && redMax >= greenMax) {
			max = redMax;
		}
		else if(blueMax >= redMax && blueMax >= greenMax) {
			max = blueMax;
		}
		else if(greenMax >= redMax && greenMax >= blueMax) {
			max = greenMax;
		}
		
		if(redMin <= blueMin && redMin <= greenMin) {
			min = redMin;
		}
		else if(blueMin <= redMin && blueMin <= greenMin) {
			min = blueMin;
		}
		else if(greenMin <= blueMin && greenMin <= redMin) {
			min = greenMin;
		}
		
		System.out.println(" ");
		System.out.println(max);
		System.out.println(min);
		
		int newValRed = 255;
		int newValBlue = 255;
		int newValGreen = 255;
		
		for(int y = 0; y < height; y++) {
			for(int x = 0; x < width; x++) {
				
				Color color = image_reader.getColor(x, y);
				
				//normalisation: new intermediate val = (intermediate val - min) * 255 / (max - min)
				if(redSum[x][y] != null) {	
					redSum[x][y] = (int) ((redSum[x][y]-min)*255.0)/(max-min);
					blueSum[x][y] = (int) ((blueSum[x][y]-min)*255.0)/(max-min);
					greenSum[x][y] = (int) ((greenSum[x][y]-min)*255.0)/(max-min);	
					color = Color.color(redSum[x][y]/255.0, greenSum[x][y]/255.0, blueSum[x][y]/ 255.0);
				} else {
					color = Color.color(0,0,0);
				}
					
					cc_image_writer.setColor(x, y, color);

				}
			}

		return cc_image;
		
	}
	
	
    public static void main(String[] args) {
        launch();
    }

}