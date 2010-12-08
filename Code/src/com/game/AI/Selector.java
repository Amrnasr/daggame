package com.game.AI;

import java.util.Vector;

import android.util.Log;

public class Selector extends Task 
{
	private Vector<Task> subtasks;
	private Task curTask;
	
	public Selector(Task parent)
	{
		super(parent);
		this.subtasks = new Vector<Task>();
		this.curTask = null;
	}

	@Override
	public boolean CheckConditions() 
	{
		return this.subtasks.size() > 0;
	}

	@Override
	public void DoAction() 
	{
		if(this.curTask == null)
		{
			// If there is a null task, we've done something wrong
			Log.e("Selector", "Current task has a null action");
			return;
		}
		
		if(this.curTask.Finished())
		{
			this.curTask.End();
			
			if(this.curTask.Succeeded())
			{
				this.FinishWithSuccess();
			}
			if(this.curTask.Failed())
			{
				this.curTask = ChooseNewTask();
				if(this.curTask == null)
				{
					this.FinishWithFailure();
				}
				else
				{
					this.curTask.Start();
				}
			}
		}
		else
		{		
			// Update the current child task		
			this.curTask.DoAction();
		}

		
		
	}

	@Override
	public void Start() 
	{
		this.curTask = this.subtasks.firstElement();
	}

	
	public void Add(Task task)
	{
		this.subtasks.add(task);
	}
	
	public Task ChooseNewTask()
	{
		Task task = null;
		boolean found = false;
		int curPos = this.subtasks.indexOf(this.curTask);
		
		while(!found)
		{
			if(curPos == (this.subtasks.size() - 1))
			{
				found = true;
				task = null;
				break;
			}
			
			task = this.subtasks.elementAt(curPos);
			if(task.CheckConditions())
			{
				found = true;
			}
			else
			{
				curPos++;
			}
		}
		
		return task;
	}
}

/*
 * @Override
	public void DoAction() 
	{
		if(this.curTask == null)
		{
			return;
		}
		
		if( this.curTask.Succeeded() )
		{
			// We're done here bail with success
			this.FinishWithSuccess();
		}
		else if( this.curTask.Failed() )
		{
			// Got to get a new one to try
			
			// Position of the current task in the vector
			int curPos = this.subtasks.indexOf(this.curTask);
			
			if(curPos == this.subtasks.size())
			{
				// Last task of them all, bail with failure
				this.FinishWithFailure();
			}
			else
			{
				this.curTask = this.subtasks.elementAt(curPos + 1);
				this.curTask.Start();
			}
		}
		else
		{
			// Update the current one
			this.curTask.DoAction();
		}
	}
 * */
