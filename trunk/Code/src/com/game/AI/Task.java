package com.game.AI;

import android.util.Log;


public abstract class Task 
{
	private boolean done;
	private boolean sucess;
	private boolean started;
	protected Blackboard bb;
	protected String name;
	
	public Task(Blackboard blackboard)
	{
		Initialize(blackboard);
	}
	
	public Task(Blackboard blackboard, String name)
	{
		this.name = name;
		Initialize(blackboard);
	}
	
	private void Initialize(Blackboard blackboard)
	{
		this.done = false;
		this.sucess = true;
		this.started = false;
		this.bb = blackboard;
	}
	
	public abstract void Start();
	public abstract void End();
	
	public void SafeStart()
	{
		this.started = true;
		Start();
	}
	
	public void SafeEnd()
	{
		this.done = false;
		this.started = false;
		End();
	}
	
	protected void FinishWithSuccess()
	{
		this.sucess = true;
		this.done = true;
		LogTask("Finished with success");
	}
	
	protected void FinishWithFailure()
	{
		this.sucess = false;
		this.done = true;
		LogTask("Finished with failure");
	}
	
	public abstract boolean CheckConditions();
	
	public abstract void DoAction();
	
	public boolean Succeeded() 
	{
		return this.sucess;
	}
	
	public boolean Failed()
	{
		return !this.sucess;
	}
	
	public boolean Finished() 
	{
		return this.done;
	}
	
	public boolean Started()
	{
		return this.started;
	}
	
	public void Reset()
	{
		this.done = false;
	}
	
	public void LogTask(String text)
	{
		Log.i("Task", "Task: " + name + "; Player: " + bb.player.GetID() + "; " + text);
	}
}
