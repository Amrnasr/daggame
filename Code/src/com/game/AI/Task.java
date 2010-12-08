package com.game.AI;

public abstract class Task 
{
	private boolean done;
	private boolean sucess;
	private Task parent;
	
	public Task(Task parent)
	{
		this.done = false;
		this.sucess = true;
		this.parent = parent;
	}
	
	public abstract void Start();
	public void End()
	{
		this.done = false;
	}
	
	protected void FinishWithSuccess()
	{
		this.sucess = true;
		this.done = true;
	}
	
	protected void FinishWithFailure()
	{
		this.sucess = false;
		this.done = true;
	}
	
	public Task GetParent() { return this.parent; }
	
	public abstract boolean CheckConditions();
	
	public abstract void DoAction();
	
	public boolean Succeeded() 
	{
		return this.done && this.sucess;
	}
	
	public boolean Failed()
	{
		return this.done && !this.sucess;
	}
	
	public boolean Finished() 
	{
		return this.done;
	}
}
