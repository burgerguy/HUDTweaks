package com.github.burgerguy.hudtweaks.hud.tree;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jetbrains.annotations.Nullable;

import com.github.burgerguy.hudtweaks.hud.HTIdentifier;
import com.github.burgerguy.hudtweaks.hud.UpdateEvent;

import net.minecraft.client.MinecraftClient;

public abstract class AbstractTypeNode {
	protected transient final HTIdentifier.ElementType elementIdentifier;
	protected transient final Set<AbstractTypeNode> xTreeChildren = new HashSet<>();
	protected transient final Set<AbstractTypeNode> yTreeChildren = new HashSet<>();
	
	private transient boolean requiresUpdate;
	
	public AbstractTypeNode(HTIdentifier.ElementType elementIdentifier) {
		this.elementIdentifier = elementIdentifier;
	}
	
	public final HTIdentifier.ElementType getElementIdentifier() {
		return elementIdentifier;
	}
	
	public Set<? extends AbstractTypeNode> getXChildren() {
		return xTreeChildren;
	}
	
	public Set<? extends AbstractTypeNode> getYChildren() {
		return yTreeChildren;
	}
	
	public abstract <T extends AbstractTypeNodeEntry> T getActiveEntry();
	
	public abstract List<? extends AbstractTypeNodeEntry> getRawEntryList();
	
	/**
	 * Passing null to the UpdateEvent will try a manual update.
	 */
	public void tryUpdateX(@Nullable UpdateEvent event, MinecraftClient client, boolean parentUpdated, Set<AbstractTypeNode> updatedElementsX) {
		boolean selfUpdated = false;
		if (!updatedElementsX.contains(this)) {
			if (parentUpdated || requiresUpdate) {
				getActiveEntry().updateSelfX(client);
				updatedElementsX.add(this);
				selfUpdated = true;
			} else {
				AbstractTypeNodeEntry activeEntry = getActiveEntry();
				if (activeEntry.shouldUpdateOnEvent(event)) {
					activeEntry.updateSelfX(client);
					updatedElementsX.add(this);
					selfUpdated = true;
				}
			}
		}
		
		for (AbstractTypeNode child : xTreeChildren) {
			child.tryUpdateX(event, client, selfUpdated, updatedElementsX);
		}
	}
	
	/**
	 * Passing null to the UpdateEvent will try a manual update.
	 */
	public void tryUpdateY(@Nullable UpdateEvent event, MinecraftClient client, boolean parentUpdated, Set<AbstractTypeNode> updatedElementsY) {
		boolean selfUpdated = false;
		if (!updatedElementsY.contains(this)) {
			if (parentUpdated || requiresUpdate) {
				getActiveEntry().updateSelfY(client);
				updatedElementsY.add(this);
				selfUpdated = true;
			} else {
				AbstractTypeNodeEntry activeEntry = getActiveEntry();
				if (activeEntry.shouldUpdateOnEvent(event)) {
					activeEntry.updateSelfY(client);
					updatedElementsY.add(this);
					selfUpdated = true;
				}
			}
		}
		
		for (AbstractTypeNode child : yTreeChildren) {
			child.tryUpdateY(event, client, selfUpdated, updatedElementsY);
		}
	}
	
	public void setRequiresUpdate() {
		requiresUpdate = true;
	}
	
	/**
	 * Do not touch unless you know what you're doing.
	 */
	public void setUpdated() {
		requiresUpdate = false;
	}
}
