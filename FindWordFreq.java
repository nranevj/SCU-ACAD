/*
Given a file, the following program can find the total occurences of the given word in the file
*/

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class Multithreading extends Thread
{   
    int counter;
    static int totalCounter = 0;
    String word;
    List<String> list;
    
	/* Initializes counter for the respective thread
	 * Initializes the list that contains the segment
	 * Initializes the word that has to be searched
	 */
    Multithreading(List<String> temp, String word)
    {
        this.counter = 0;
        this.list = temp;
        this.word = word;
    }
	/* Automatically called when start() method is called in main()
	 * It gets the frequency of the word for each line from 'countFreq()' for its respective threads,
	 * and calculates the total freq of all the threads which is stored in 'totalCounter'
	 */
    public void run()
    { 
        for(int i = 0; i < this.list.size();i++)
            this.counter = this.counter + countFreq(this.list.get(i), this.word);
        
        System.out.println("Thread with pid:"+Thread.currentThread().getId()+" has "+this.counter+" occurences of "+this.word);
        totalCounter = totalCounter + this.counter;
    }
    // Counts and returns the frequency of word in a line
    static int countFreq(String line, String word)
    {
        Pattern p;
        Matcher m;
        int counter = 0;
        
        for(String l: line.split("\\s+"))
        {
            p = Pattern.compile(word.toLowerCase());
            m = p.matcher(l.toLowerCase());
            
            if(m.find())
                counter++;
        }
        return counter;
    }
}

public class FindWordFreq
{
    public static void main(String args[]) throws IOException, InterruptedException, FileNotFoundException
    {
		//These parameters can be changed (dynamic inputs accepted)
        int numofThreads = 5;
        String file = "C:\\InputFileAssignment1.txt";
        String word = "Glassdoor";
        
        List<List<String>> linesegment = new ArrayList<>();
        BufferedReader br = new BufferedReader(new FileReader(file));
    
        String line;
        int numofLines = 0;
        List<String> temp = new ArrayList();
        
		//Reading all the lines in one list, and determining the number of lines as well
        while((line = br.readLine())!= null)
        {
            temp.add(line);
            numofLines++;
        }
        
		//Making sure the threads don't exceed the number of lines
        if(numofThreads > numofLines)
			numofThreads = numofLines;
    
        int sizeofSeg = numofLines/numofThreads;    
        List<String> segment = new ArrayList();
        int index = 0;
        
		//Making multiple 'segment' lists and adding them to the master list 'linesegment'
        while(index < numofLines)
        {
            segment.add(temp.get(index));
            index++;
            
            if(index % sizeofSeg == 0)
            {
                linesegment.add(segment);
                segment = new ArrayList();
            }
        }
 
		//Making sure that all segments are covered and will run in 'numofThreads' threads
        if((numofLines % numofThreads)!=0)
        {
            for(String i : segment)
            {
                linesegment.get(numofThreads - 1).add(i);
            }
        }
        List<Thread> threads = new ArrayList();
        
        for(int i = 0; i < numofThreads;i++)
        {
            Multithreading mt = new Multithreading(linesegment.get(i), word);
            threads.add(mt);
            mt.start();
        }
        
		//Will wait for all the processes called using start() to die, then only the main will move forward
        for(Thread thread:threads)
            thread.join();
        
        System.out.println("Total occurrences of "+word+" is "+Multithreading.totalCounter);
    }
}