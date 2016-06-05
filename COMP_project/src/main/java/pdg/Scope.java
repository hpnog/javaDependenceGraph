package pdg;

import java.util.ArrayList;

import graphStructures.VarChanges;

class Scope {
	ArrayList<VarChanges> varChanges = new ArrayList<>();
	ArrayList<VarChanges> varAccesses = new ArrayList<>();
}
