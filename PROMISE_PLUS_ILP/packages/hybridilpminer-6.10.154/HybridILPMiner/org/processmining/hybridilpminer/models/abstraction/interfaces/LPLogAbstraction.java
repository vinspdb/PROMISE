package org.processmining.hybridilpminer.models.abstraction.interfaces;

import gnu.trove.map.TObjectIntMap;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * An LPLogAbstraction is an abstraction of an event log which is in line with
 * the general abstraction used in (I)LP-based Process Discovery. Each event
 * class in the log will be mapped to some integer. Each trace in a log will be
 * mapped to a multi-set representing parikh values of the prefix of the trace
 * and a single element representing the final activity. For example if we have
 * trace <a,b,c> with mapping a -> 0, b -> 1, c -> 2 (i.e. we only have event
 * classes a,b and c in the event log) then this trace maps to the array
 * [1,1,0,2].
 * 
 * @author svzelst
 *
 * @param <T>
 *            Generic indicator for the type used to identify an activity, e.g.
 *            XEventClass
 */
public interface LPLogAbstraction<T> {

	/**
	 * Returns the cardinality of a word, where the word is given as its integer
	 * representation.
	 *
	 * @param w
	 *            integer representation of the word
	 * @return
	 */
	public int cardinality(int[] w);

	/**
	 * Returns the cardinality of a trace
	 *
	 * @param w
	 *            word
	 * @return cardinality of word w
	 */
	public int cardinality(List<T> w);

	/**
	 * Get back the event belonging to this index.
	 * 
	 * @param i
	 *            encoding of some event
	 * @return event belonging to encoding
	 */
	public T decode(int i);

	/**
	 * Get back all events belonging to the indices in the set
	 * 
	 * @param is
	 *            set of indices
	 * @return set of events corresponding to the indices
	 */
	public Set<T> decode(Set<Integer> is);

	/**
	 * What events are present in the log?
	 * 
	 * @return set of events present in the log
	 */
	public Set<T> domain();

	/**
	 * Encode this sequence to an abstraction
	 * 
	 * @param l
	 * @return
	 */
	public int[] encode(List<T> l);

	/**
	 * Given a set of objects, return the encoding characters.
	 * 
	 * @param ts
	 * @return
	 */
	public Set<Integer> encode(Set<T> ts);

	/**
	 * To what integer is the given event mapped?
	 * 
	 * @param event
	 *            input for query
	 * @return encoding of event
	 */
	public int encode(T t);

	/**
	 * Get the abstraction of the event log
	 * 
	 * @return mapping of abstractions to frequencies
	 */
	public TObjectIntMap<int[]> eventLogAbstraction();

	/**
	 * Get all full traces mapping to their corresponding abstractions
	 * 
	 * @return
	 */
	public Map<int[], Set<Set<int[]>>> tracePrefixMapping();

	/**
	 * Get the abstraction of the prefix-closure of the event log
	 * 
	 * @return mapping of abstractions to frequencies
	 */
	public TObjectIntMap<int[]> prefixClosureAbstraction();

	/**
	 * Encoding of all events in internal representation.
	 * 
	 * @return array of integers, used to represent events
	 */
	public int[] range();

}
