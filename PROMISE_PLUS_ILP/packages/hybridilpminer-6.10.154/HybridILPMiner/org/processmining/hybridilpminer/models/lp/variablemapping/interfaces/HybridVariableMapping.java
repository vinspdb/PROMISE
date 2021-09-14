package org.processmining.hybridilpminer.models.lp.variablemapping.interfaces;

import java.util.Set;

/**
 * A hybrid variable mapping, maps some object to an LP index, according to the
 * definition of hybrid regions. From a definition point of view the generic
 * type parameter T refers to the type of the "letters" / "events" used within
 * the language / log on top of which we build an ILP.
 * 
 * @author svzelst
 *
 * @param <T>
 *            refers to the type of the "letters" / "events" used within the
 *            language / log on top of which we build an ILP.
 */
public interface HybridVariableMapping<T> extends VariableMapping {

	public Set<T> getDomain();

	public Set<T> getDualVariableObjects();

	public int getMarkingVariableLPIndex();

	public T getObjectOfLpIndex(int i);

	public int getSingleVariableIndexOf(T t);

	public int[] getSingleVariableIndices();

	public Set<T> getSingleVariables();

	public int getXVariableIndexOf(T t);

	public int[] getXVariableIndices();

	public int getYVariableIndexOf(T t);

	public int[] getYVariableIndices();

	public boolean isDual();

	public boolean isDualVariableIndex(int i);

	public boolean isDualVariableObject(T t);

	public boolean isEventRelatedVariableIndex(int i);

	public boolean isHybrid();

	public boolean isSingle();

	public boolean isSingleVariableIndex(int i);

	public boolean isSingleVariableObject(T t);

	public boolean isXVariableIndex(int i);

	public boolean isYVariableIndex(int i);

	public double[] projectOnHybridVariableIndices(double[] solution);
}
