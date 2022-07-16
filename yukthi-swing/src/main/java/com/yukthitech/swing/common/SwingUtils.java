package com.yukthitech.swing.common;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Image;
import java.awt.KeyboardFocusManager;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import org.apache.batik.transcoder.Transcoder;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.PNGTranscoder;

import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Common util methods used across the ide project.
 * @author akiran
 */
public class SwingUtils
{
	private static final int BORDER_SIZE = 8;

	private static BufferedImage loadSvg(String resource, int size)
	{
		// Create a PNG transcoder.
        Transcoder t = new PNGTranscoder();

        // Set the transcoding hints.
        t.addTranscodingHint(PNGTranscoder.KEY_WIDTH, (float) size);
        t.addTranscodingHint(PNGTranscoder.KEY_HEIGHT, (float) size);
        
        try (InputStream inputStream = SwingUtils.class.getResourceAsStream(resource)) 
        {
            // Create the transcoder input.
            TranscoderInput input = new TranscoderInput(inputStream);

            // Create the transcoder output.
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            TranscoderOutput output = new TranscoderOutput(outputStream);

            // Save the image.
            t.transcode(input, output);

            // Flush and close the stream.
            outputStream.flush();
            outputStream.close();

            // Convert the byte stream into an image.
            byte[] imgData = outputStream.toByteArray();
            return ImageIO.read(new ByteArrayInputStream(imgData));

        } catch (IOException | TranscoderException ex) 
        {
            throw new InvalidStateException("An error occurred while loading svg resource: {}", resource, ex);
        }
	}
	
	/**
	 * Loads specified resource as an icon with specified size.
	 * @param resource
	 * @param size
	 * @return
	 */
	public static ImageIcon loadIcon(String resource, int size, int borderSize)
	{
		Image baseImg = null;
		
		if(resource.toLowerCase().endsWith(".svg"))
		{
			baseImg = loadSvg(resource, size);
		}
		else
		{
			ImageIcon icon = new ImageIcon(SwingUtils.class.getResource(resource));
			baseImg = icon.getImage();
		}
		
		BufferedImage img = new BufferedImage(size + borderSize, size + borderSize, BufferedImage.TYPE_INT_ARGB);
		img.getGraphics().drawImage(baseImg, borderSize, borderSize, size, size, null);
		
		return new ImageIcon(img);
	}

	/**
	 * Loads specified resource as an icon with specified size.
	 * @param resource
	 * @param size
	 * @return
	 */
	public static ImageIcon loadIcon(String resource, int size)
	{
		return loadIcon(resource, size, BORDER_SIZE);
	}
	
	public static ImageIcon loadIconWithoutBorder(String resource, int size)
	{
		Image baseImg = null;
		
		if(resource.toLowerCase().endsWith(".svg"))
		{
			baseImg = loadSvg(resource, size);
		}
		else
		{
			ImageIcon icon = new ImageIcon(SwingUtils.class.getResource(resource));
			baseImg = icon.getImage();
		}

		if(size <= 0)
		{
			return new ImageIcon(baseImg);
		}
		
		BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
		img.getGraphics().drawImage(baseImg, 0, 0, size, size, null);
		
		return new ImageIcon(img);
	}

	public static Window getCurrentWindow()
	{
		return KeyboardFocusManager.getCurrentKeyboardFocusManager().getActiveWindow();
	}
	
	public static void executeUiTask(Runnable runnable)
	{
		EventQueue.invokeLater(runnable);
	}
	
	public static void centerOnScreen(Component c)
	{
		final int width = c.getWidth();
		final int height = c.getHeight();
		final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int x = (screenSize.width / 2) - (width / 2);
		int y = (screenSize.height / 2) - (height / 2);

		c.setLocation(x, y);
	}
	
	public static void maximize(Component c, int gap)
	{
		//double the gap, so that gap is maintained in all directions
		gap = gap * 2;
		
		final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		c.setSize(screenSize.width - gap, screenSize.height - gap);
	}
}
