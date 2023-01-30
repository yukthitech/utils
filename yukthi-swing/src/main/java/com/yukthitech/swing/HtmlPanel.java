package com.yukthitech.swing;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.net.URI;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkEvent.EventType;
import javax.swing.event.HyperlinkListener;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Panel capable of displaying html content including resource images and basic
 * forward, backward and refresh functionality.
 * 
 * @author akranthikiran
 */
public class HtmlPanel extends JPanel
{
	private static final long serialVersionUID = 1L;
	
	private static Logger logger = LogManager.getLogger(HtmlPanel.BrowsingHistory.class);
	
	private class BrowsingHistory
	{
		private List<String> locations = new ArrayList<>();
		
		private int index;
		
		public void goBack()
		{
			index --;
			
			if(index < 0)
			{
				index = -1;
				scrollPane.getVerticalScrollBar().setValue(0);
				scrollPane.getHorizontalScrollBar().setValue(0);
				return;
			}
			
			String loc = locations.get(index);
			textPane.scrollToReference(loc);
		}
		
		public void goForward()
		{
			int maxIdx = locations.size() - 1;
			index ++;
			
			if(index > maxIdx)
			{
				index = maxIdx;
			}
			
			String loc = locations.get(index);
			textPane.scrollToReference(loc);
		}
		
		public void addLocation(String loc)
		{
			if(index < (locations.size() - 1))
			{
				if(index > 0)
				{
					locations = new ArrayList<>(locations.subList(0, index));
				}
				else
				{
					locations.clear();
				}
			}
			
			locations.add(loc);
			index = locations.size() - 1;
		}
	}
	
	
	private static Pattern RES_URL_PATTERN = Pattern.compile("(\\w+)\\:\\/\\/([\\S&&[^\"]]+)");
	
	private final JScrollPane scrollPane = new JScrollPane();
	private final JTextPane textPane = new JTextPane();

	private String htmlContent = "<html></html>";
	
	private String resource;
	
	/**
	 * To be used in places where content is expected to be processed
	 * before finalization.
	 */
	private Function<String, String> contentProcessor;
	
	private BrowsingHistory browsingHistory = new BrowsingHistory();

	public HtmlPanel()
	{
		setLayout(new BorderLayout(0, 0));
		
		add(scrollPane, BorderLayout.CENTER);
		textPane.setContentType("text/html");
		textPane.setEditable(false);
		
		scrollPane.setViewportView(textPane);
		
		textPane.addKeyListener(new KeyAdapter()
		{
			@Override
			public void keyReleased(KeyEvent e)
			{
				if(e.getKeyCode() == KeyEvent.VK_F5)
				{
					reload();
					return;
				}

				if(e.getKeyCode() == KeyEvent.VK_F10)
				{
					openInBrowser();
					return;
				}
				
				if(e.isAltDown())
				{
					if(e.getKeyCode() == KeyEvent.VK_LEFT)
					{
						browsingHistory.goBack();
					}
					else if(e.getKeyCode() == KeyEvent.VK_RIGHT)
					{
						browsingHistory.goForward();
					}
					
					return;
				}
			}
		});
		
		textPane.addHyperlinkListener(new HyperlinkListener()
		{
			@Override
			public void hyperlinkUpdate(HyperlinkEvent e)
			{
				if(e.getEventType() != EventType.ACTIVATED)
				{
					return;
				}
				
				String reference = e.getDescription();
				
				if(reference == null)
				{
					return;
				}
				
				/*
				 * Note: Links can be created using anchor tags. Eg: <a href="#bookmark-id">some text</a>
				 * The referred bookmarks also has to be created using anchor tags with name attribute: Eg: <a name="bookmark-id"></a>
				 */
                if (reference.startsWith("#")) 
                {
                    reference = reference.substring(1);
                    textPane.scrollToReference(reference);
                    
                    browsingHistory.addLocation(reference);
                }
                else
                {
                	try
                	{
                		Desktop.getDesktop().browse(new URI(reference));
                	}catch(Exception ex)
                	{
                		logger.error("An error occurred while opening browser with url: {}", reference, ex);
                	}
                }
			}
		});
		
		textPane.addFocusListener(new FocusAdapter()
		{
			@Override
			public void focusGained(FocusEvent e)
			{
				textPane.getCaret().setVisible(true);
			}
		});
	}
	
	/**
	 * Create the panel.
	 */
	public HtmlPanel(String resource)
	{
		this();
		this.setResource(resource);
	}
	
	public void setContent(String staticContent)
	{
		resource = null;
		htmlContent = processNewContent(staticContent);
		textPane.setText(staticContent);
	}
	
	public void setResource(String resource)
	{
		this.resource = resource;
		reload();
	}
	
	public void setContentProcessor(Function<String, String> contentProcessor)
	{
		this.contentProcessor = contentProcessor;
		reload();
	}
	
	private String loadResource(String resource)
	{
		File resourceFile = null;
		
		try
		{
			resourceFile = new File(HtmlPanel.class.getResource(resource).toURI());
		}catch(Exception ex)
		{
			//if the uri does not represent a file path
			resourceFile = null;
		}
		
		try
		{
			String content = null;
			
			if(resourceFile != null && resourceFile.exists())
			{
				content = FileUtils.readFileToString(resourceFile, Charset.defaultCharset());
			}
			else
			{
				content = IOUtils.toString(HtmlPanel.class.getResourceAsStream(resource), Charset.defaultCharset());				
			}
			
			return content;
		} catch(Exception ex)
		{
			throw new IllegalStateException("An error ocurred while loading resource: " + resource, ex);
		}
	}

	private void reload()
	{
		if(resource != null)
		{
			htmlContent = loadResource(resource);
			htmlContent = processNewContent(htmlContent);
		}

		textPane.setText(htmlContent);
	}
	
	private String processNewContent(String content)
	{
		if(contentProcessor != null)
		{
			content = contentProcessor.apply(content);
		}
		
		content = replaceUrls(content);
		return content;
	}
	
	private String replaceUrls(String content)
	{
		Matcher matcher = RES_URL_PATTERN.matcher(content);
		StringBuffer buffer = new StringBuffer();
		
		while(matcher.find())
		{
			String urlType = matcher.group(1);
			String resPath = matcher.group(2);
			
			if("string".equals(urlType))
			{
				matcher.appendReplacement(buffer, resPath);
			}
			else if("res".equals(urlType))
			{
				URL urlObj = HtmlPanel.class.getResource(resPath);
				
				if(urlObj == null)
				{
					throw new InvalidStateException("Invalid resource url '{}' is referred in content", resPath);
				}
				
				String url = urlObj.toString();
				matcher.appendReplacement(buffer, url);
			}
		}
		
		matcher.appendTail(buffer);
		return buffer.toString();
	}
	
	private void openInBrowser()
	{
		try
		{
			File tmpFile = File.createTempFile("tmp", ".html");
			FileUtils.write(tmpFile, htmlContent, Charset.defaultCharset());
			
			Desktop.getDesktop().open(tmpFile);
		}catch(Exception ex)
		{
			JOptionPane.showMessageDialog(this, "An error occurred while opening content in browser. \nError: " + ex);
		}
		
	}
}
