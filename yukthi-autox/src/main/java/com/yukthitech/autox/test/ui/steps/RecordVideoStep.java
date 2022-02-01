package com.yukthitech.autox.test.ui.steps;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;
import org.jcodec.api.awt.AWTSequenceEncoder;
import org.jcodec.common.io.NIOUtils;
import org.jcodec.common.io.SeekableByteChannel;
import org.jcodec.common.model.Rational;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import com.yukthitech.autox.AbstractStep;
import com.yukthitech.autox.AutomationContext;
import com.yukthitech.autox.Executable;
import com.yukthitech.autox.ExecutionLogger;
import com.yukthitech.autox.Group;
import com.yukthitech.autox.IStep;
import com.yukthitech.autox.IStepContainer;
import com.yukthitech.autox.IStepListener;
import com.yukthitech.autox.Param;
import com.yukthitech.autox.common.SkipParsing;
import com.yukthitech.autox.config.SeleniumPlugin;
import com.yukthitech.autox.test.Function;
import com.yukthitech.autox.test.TestCaseData;
import com.yukthitech.autox.test.log.LogLevel;
import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Loops through specified range of values and for each iteration executed underlying steps
 * 
 * @author akiran
 */
@Executable(name = "recordVideo", group = Group.Ui, message = "Records the browser screen as video for all the steps under current step")
public class RecordVideoStep extends AbstractStep implements IStepContainer
{
	private static final long serialVersionUID = 1L;
	
	private static final Font MSSG_FONT = new Font(Font.SANS_SERIF, Font.BOLD, 14);
	
	private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("hh:mm:ss aa");

	/**
	 * Group of steps/validations to be executed as part of this step.
	 */
	@SkipParsing
	@Param(description = "Group of steps/validations to be executed in loop.")
	private Function steps;
	
	/**
	 * Name of the video file to be created.
	 */
	@Param(description = "Name of the video file to be created. May get suffixed to create unique file.")
	private String name;
	
	/**
	 * Video speed in frames per second. Defaults to 1.
	 */
	@Param(description = "Video speed in frames per second. Defaults to 1.")
	private Integer framesPerSec = 1;

	/* (non-Javadoc)
	 * @see com.yukthitech.autox.IStepContainer#addStep(com.yukthitech.autox.IStep)
	 */
	@Override
	public void addStep(IStep step)
	{
		if(steps == null)
		{
			steps = new Function();
		}
		
		steps.addStep(step);
	}
	
	@Override
	public List<IStep> getSteps()
	{
		return steps.getSteps();
	}
	
	public void setName(String name)
	{
		this.name = name;
	}
	
	public void setFramesPerSec(Integer framesPerSec)
	{
		if(framesPerSec <= 0)
		{
			throw new IllegalArgumentException("Value should be greater than zero.");
		}
		
		this.framesPerSec = framesPerSec;
	}

	@Override
	public boolean execute(AutomationContext context, ExecutionLogger exeLogger) throws Exception
	{
		exeLogger.debug("Recording started with name: {}", name);
		
		File videoFile = exeLogger.createFile(name, ".mp4"); 
		SeekableByteChannel channel = NIOUtils.writableChannel(videoFile);
		
		AWTSequenceEncoder encoder = new AWTSequenceEncoder(channel, Rational.R(framesPerSec, 1));
		SeleniumPlugin seleniumConfiguration = context.getPlugin(SeleniumPlugin.class);
		WebDriver driver = seleniumConfiguration.getWebDriver();
	
		IStepListener listener = new IStepListener()
		{
			private void addStepImage(IStep step, String message)
			{
				Executable executable = step.getClass().getAnnotation(Executable.class);
				
				if(executable == null || executable.group() != Group.Ui)
				{
					return;
				}
				
				String stepText = step.toString();
				String stepDet = String.format("[%s] [%s] %s", TIME_FORMAT.format(new Date()), step.getLocation(), stepText);
				File file = null;
						
				try
				{
					file = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
				}catch(Exception ex)
				{
					return;
				}

				try
				{
					BufferedImage img = ImageIO.read(file);
					FileUtils.forceDelete(file);
					
					int width = img.getWidth();
					width = (width % 2 == 0) ? width : (width + 1);
					
					int height = img.getHeight() + 100;
					height = (height % 2 == 0) ? height : (height + 1);

					BufferedImage imgWithMssg = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
					Graphics g = imgWithMssg.getGraphics();
					
					g.drawImage(img, 0, 0, null);
					g.setColor(Color.white);
					g.setFont(MSSG_FONT);
					g.drawString(stepDet, 10, img.getHeight() + 15);
					g.drawString(message, 20, img.getHeight() + 15 + 20);
					
					encoder.encodeImage(imgWithMssg);
				}catch(Exception ex)
				{
					exeLogger.error("Adding img with mssg: '{}' resulted in error", stepDet, ex);
					throw new InvalidStateException("An error occurred while adding current screen shot to video", ex);
				}
			}
			
			@Override
			public void stepStarted(IStep step, TestCaseData data)
			{
				addStepImage(step, "Step Started.");
			}
			
			@Override
			public void stepPhase(IStep step, String mssg)
			{
				addStepImage(step, mssg);
			}
			
			@Override
			public void stepErrored(IStep step, TestCaseData data, Exception ex)
			{
				addStepImage(step, "Step Errored - " + ex);
			}
			
			@Override
			public void stepCompleted(IStep step, TestCaseData data)
			{
				addStepImage(step, "Step Completed.");
			}
		};
		
		context.addStepListener(listener);
		
		try
		{
			steps.execute(context, exeLogger, true);
		}finally
		{
			// Finalize the encoding, i.e. clear the buffers, write the header, etc.
		    encoder.finish();
		    NIOUtils.closeQuietly(channel);
		    
			context.removeStepListener(listener);
			exeLogger.logFile("Recoding completed and can be seen in below file", LogLevel.DEBUG, videoFile);
		}
		
		return true;
	}
}
