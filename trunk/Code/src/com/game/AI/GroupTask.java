package com.game.AI;

import java.util.Vector;

import android.util.Log;

public abstract class GroupTask extends Task 
{
	protected Vector<Task> subtasks;
	protected Task curTask;
	
	public GroupTask(Blackboard blackboard)
	{
		super(blackboard);
		Initialize();
	}
	
	public GroupTask(Blackboard blackboard, String name)
	{
		super(blackboard, name);
		Initialize();
	}
	
	private void Initialize()
	{
		this.subtasks = new Vector<Task>();
		this.curTask = null;
	}

	@Override
	public boolean CheckConditions() 
	{
		LogTask("Checking conditions");
		return this.subtasks.size() > 0;
	}
	
	public abstract void ChildSucceeded();
	public abstract void ChildFailed();

	@Override
	public void DoAction() 
	{
		LogTask("Doing action");
		if(this.Finished())
		{
			return;
		}
		if(this.curTask == null)
		{
			// If there is a null task, we've done something wrong
			Log.e("Selector", "Current task has a null action");
			return;
		}
		
		// If we do have a curTask...
		if( !this.curTask.Started())
		{
			// ... and it's not started yet, start it.
			this.curTask.SafeStart();
		}		
		else if(this.curTask.Finished())
		{
			// ... and it's finished, end it properly.
			this.curTask.SafeEnd();
			
			if(this.curTask.Succeeded())
			{
				this.ChildSucceeded();
			}
			if(this.curTask.Failed())
			{
				this.ChildFailed();
			}
		}
		else
		{		
			// ... and it's ready, update it.		
			this.curTask.DoAction();
		}	
	}

	@Override
	public void End() 
	{
		LogTask("Ending");
	}

	@Override
	public void Start() 
	{
		LogTask("Starting");
		this.curTask = this.subtasks.firstElement();
		if(this.curTask == null)
		{
			Log.e("Selector", "Current task has a null action");
		}
	}
	
	public void Add(Task task)
	{
		this.subtasks.add(task);
	}

}
