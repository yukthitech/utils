package com.yukthitech.autox.ide.editor;

import java.awt.Point;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.text.BadLocationException;

import org.apache.commons.collections.CollectionUtils;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rtextarea.Gutter;
import org.fife.ui.rtextarea.GutterIconInfo;
import org.fife.ui.rtextarea.IconRowHeader;
import org.springframework.beans.factory.annotation.Autowired;

import com.yukthitech.autox.ide.IdeUtils;
import com.yukthitech.autox.ide.editor.FileEditorIconGroup.FileEditorIcon;
import com.yukthitech.autox.ide.editor.FileEditorIconGroup.IconType;
import com.yukthitech.autox.ide.exeenv.debug.DebugManager;
import com.yukthitech.autox.ide.exeenv.debug.DebugPoint;
import com.yukthitech.autox.ide.model.Project;
import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Manages icons and debug points for file editor.
 * @author akranthikiran
 */
public class FileEditorIconManager
{
	private static ImageIcon DEBUG_POINT_ICON = IdeUtils.loadIconWithoutBorder("/ui/icons/debug-point.svg", 12);

	@Autowired
	private DebugManager debugManager;

	private RSyntaxTextArea syntaxTextArea;
	
	private File file;
	
	private Project project;
	
	private List<FileEditorIconGroup> editorIconGroups = new ArrayList<>();

	/**
	 * Flag indicating if execution/debugging is supported in this file.
	 */
	private boolean executionSupported;

	private IconRowHeader iconArea;
	
	private Gutter gutter;

	public FileEditorIconManager(FileEditor fileEditor, boolean executionSupported, IconRowHeader iconArea, Gutter gutter)
	{
		this.file = fileEditor.getFile();
		this.project = fileEditor.getProject();

		this.syntaxTextArea = fileEditor.getTextArea();
		this.executionSupported = executionSupported;
		this.iconArea = iconArea;
		this.gutter = gutter;
	}
	
	public boolean isExecutionSupported()
	{
		return executionSupported;
	}
	
	void loadDebugPoints()
	{
		if(!executionSupported)
		{
			return;
		}
		
		List<DebugPoint> points = debugManager.getDebugPoints(file);
		
		if(CollectionUtils.isEmpty(points))
		{
			return;
		}
		
		points.forEach(point -> addDebugPoint(point));
	}
	
	void clearNonDebugIcons()
	{
		List<FileEditorIconGroup> groupsToRemove = new ArrayList<>();
		
		for(FileEditorIconGroup grp : this.editorIconGroups)
		{
			grp.clearNonDebugIcons();
			
			if(grp.isEmpty())
			{
				gutter.removeTrackingIcon(grp.getIconInfo());
				groupsToRemove.add(grp);
			}
		}
		
		this.editorIconGroups.removeAll(groupsToRemove);
	}

	private void addDebugPoint(DebugPoint debugPoint)
	{
		FileEditorIcon iconInfo = addIcon(debugPoint.getLineNo(), DEBUG_POINT_ICON, null, IconType.DEBUG);
		
		if(iconInfo == null)
		{
			throw new InvalidStateException("Debug points are not in sync - {}:{}", debugPoint.getFile().getName(), debugPoint.getLineNo());
		}
		
		iconInfo.setDebugPoint(debugPoint);
	}

	private int getLineNo(Point point)
	{
		int viewPort = syntaxTextArea.viewToModel(point);
		
		if(viewPort < 0)
		{
			return -1;
		}
		
		try
		{
			return syntaxTextArea.getLineOfOffset(viewPort);
		}catch(Exception ex)
		{
			return -1;
		}
	}
	
	private void toggleBreakPoint(int lineNo)
	{
		if(!executionSupported)
		{
			return;
		}
		
		if(lineNo < 0 || lineNo >= syntaxTextArea.getLineCount())
		{
			return;
		}
		
		resetDebugPoints();
		
		try
		{
			int offset = syntaxTextArea.getLineStartOffset(lineNo);
			FileEditorIconGroup grp = this.editorIconGroups
					.stream()
					.filter(edtGrp -> edtGrp.getIconInfo().getMarkedOffset() == offset).findFirst()
					.orElse(null);
			
			FileEditorIcon existingDebugIcon = grp == null ? null : grp.getDebugIcon();
			
			if(existingDebugIcon == null)
			{
				DebugPoint debugPoint = debugManager.addDebugPoint(project.getName(), file, lineNo);
				addDebugPoint(debugPoint);
			}
			else
			{
				debugManager.removeBreakPoint(existingDebugIcon.getDebugPoint());
				removeIcon(grp, existingDebugIcon);
			}
		}catch(BadLocationException ex)
		{
			throw new InvalidStateException("An error occurred while fetching debug points info", ex);
		}
	}
	
	void resetDebugPoints()
	{
		if(!executionSupported)
		{
			return;
		}
		
		try
		{
			Map<Integer, FileEditorIconGroup> posToIcon = new HashMap<>();
			List<FileEditorIconGroup> iconsToRemove = new ArrayList<>();
					
			for(FileEditorIconGroup iconGrp : this.editorIconGroups)
			{
				int pos = iconGrp.getIconInfo().getMarkedOffset();
				FileEditorIconGroup existingGrp = posToIcon.get(pos);
				
				if(existingGrp != null)
				{
					iconsToRemove.add(existingGrp);
					continue;
				}
				
				FileEditorIcon debugIcon = iconGrp.getDebugIcon();
				
				if(debugIcon != null)
				{
					int lineNo = syntaxTextArea.getLineOfOffset(pos);
					debugIcon.getDebugPoint().setLineNo(lineNo);
				}
				
				posToIcon.put(pos, iconGrp);
			}
			
			for(FileEditorIconGroup iconGrpToRem : iconsToRemove)
			{
				FileEditorIcon debugIcon = iconGrpToRem.getDebugIcon();
				
				if(debugIcon != null)
				{
					debugManager.removeBreakPoint(debugIcon.getDebugPoint());
				}
				
				iconArea.removeTrackingIcon(iconGrpToRem.getIconInfo());
			}
			
			this.editorIconGroups.removeAll(iconsToRemove);
		}catch(Exception ex)
		{
			throw new InvalidStateException("An error occurred while reseting debug points", ex);
		}
	}

	FileEditorIcon addIcon(int lineNo, Icon icon, String mssg, IconType iconType)
	{
		try
		{
			int offset = syntaxTextArea.getLineStartOffset(lineNo);
			
			//fetch icon group at same position
			FileEditorIconGroup existingGroup = this.editorIconGroups.stream()
					.filter(grp -> grp.getIconInfo().getMarkedOffset() == offset)
					.findFirst()
					.orElse(null);
			
			//if group does not exist create new one
			if(existingGroup == null)
			{
				existingGroup = new FileEditorIconGroup();
				this.editorIconGroups.add(existingGroup);
			}

			//remove existing old icons from the line (this will clean up missed icons also)
			GutterIconInfo oldIcons[] = iconArea.getTrackingIcons(lineNo);
			
			if(oldIcons.length > 0)
			{
				for(GutterIconInfo oldIcon : oldIcons)
				{
					iconArea.removeTrackingIcon(oldIcon);
				}
			}
			
			//add new icon group
			FileEditorIcon editorIcon = new FileEditorIcon(iconType, icon, mssg);
			existingGroup.addIcon(editorIcon);

			GutterIconInfo info = gutter.addLineTrackingIcon(lineNo, existingGroup, existingGroup.getMessage());
			existingGroup.setIconInfo(info);
			
			return editorIcon;
		}catch(BadLocationException ex)
		{
			throw new InvalidStateException("An error occurred while adding icon", ex);
		}
	}
	
	private void removeIcon(FileEditorIconGroup grp, FileEditorIcon icon)
	{
		if(!grp.removeIcon(icon))
		{
			return;
		}
		
		int pos = grp.getIconInfo().getMarkedOffset();
		iconArea.removeTrackingIcon(grp.getIconInfo());
		
		if(grp.isEmpty())
		{
			editorIconGroups.remove(grp);
			return;
		}
		
		try
		{
			iconArea.addOffsetTrackingIcon(pos, grp, grp.getMessage());
		} catch(BadLocationException ex)
		{
			throw new InvalidStateException("An error occurred while removing icon", ex);
		}
	}

	public void toggleBreakPoint(Point point)
	{
		int lineNo = getLineNo(point);
		
		if(lineNo < 0)
		{
			return;
		}
		
		toggleBreakPoint(lineNo);
	}
}