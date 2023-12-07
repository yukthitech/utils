/**
 * Copyright (c) 2022 "Yukthi Techsoft Pvt. Ltd." (http://yukthitech.com)
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.yukthitech.jexpr;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class LibraryContext
{
	public static class Book
	{
		private String title;
		
		private String author;
		
		private String type;
		
		private int copyCount;
		
		public String getTitle()
		{
			return title;
		}

		public void setTitle(String title)
		{
			this.title = title;
		}

		public String getAuthor()
		{
			return author;
		}

		public void setAuthor(String author)
		{
			this.author = author;
		}

		public String getType()
		{
			return type;
		}

		public void setType(String type)
		{
			this.type = type;
		}

		public int getCopyCount()
		{
			return copyCount;
		}

		public void setCopyCount(int copyCount)
		{
			this.copyCount = copyCount;
		}
	}
	
	public static class Library
	{
		private String name;
		
		private List<Book> books = new ArrayList<>();
		
		private boolean open;

		public String getName()
		{
			return name;
		}

		public void setName(String name)
		{
			this.name = name;
		}

		public List<Book> getBooks()
		{
			return books;
		}

		public void setBooks(List<Book> books)
		{
			this.books = books;
		}
		
		public void addBook(Book book)
		{
			this.books.add(book);
		}

		public boolean isOpen()
		{
			return open;
		}

		public void setOpen(boolean open)
		{
			this.open = open;
		}
	}
	
	private Map<String, Library> libraries = new LinkedHashMap<>();
	
	private String ckey1;

	public Map<String, Library> getLibraries()
	{
		return libraries;
	}

	public void setLibraries(Map<String, Library> libraries)
	{
		this.libraries = libraries;
	}
	
	public void addLibrary(Library library)
	{
		this.libraries.put(library.getName(), library);
	}
	
	public int getOpenLibraryCount()
	{
		return (int) this.libraries.values().stream().filter(lib -> lib.open).count();
	}
	
	public List<Library> getLibraryList()
	{
		return new ArrayList<>(this.libraries.values());
	}

	public String getCkey1()
	{
		return ckey1;
	}

	public void setCkey1(String ckey1)
	{
		this.ckey1 = ckey1;
	}
}
