package pdg;

import java.util.ArrayList;

import graphStructures.VarChanges;

/**
 * The Class Scope.
 */
class Scope {
	
	/** The var changes. */
	ArrayList<VarChanges> varChanges = new ArrayList<>();
	
	/** The var accesses. */
	ArrayList<VarChanges> varAccesses = new ArrayList<>();
}
