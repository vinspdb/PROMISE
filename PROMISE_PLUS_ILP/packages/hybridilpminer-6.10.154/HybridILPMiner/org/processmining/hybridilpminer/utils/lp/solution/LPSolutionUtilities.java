package org.processmining.hybridilpminer.utils.lp.solution;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.deckfour.xes.classification.XEventClass;
import org.processmining.framework.util.Pair;
import org.processmining.hybridilpminer.models.abstraction.interfaces.LPLogAbstraction;
import org.processmining.hybridilpminer.models.lp.variablemapping.interfaces.HybridVariableMapping;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.graphbased.directed.petrinet.elements.Place;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.models.graphbased.directed.petrinet.impl.PetrinetFactory;
import org.processmining.models.semantics.petrinet.Marking;

public class LPSolutionUtilities {

	public static boolean isTrivial(double[] solution) {
		boolean result = true;
		for (double d : solution) {
			if (d != 0) {
				result = false;
				break;
			}
		}
		return result;
	}

	public static <T extends HybridVariableMapping<?>> boolean isOnlyMarking(T varMap, double[] solution) {
		boolean result = true;
		result &= solution[varMap.getMarkingVariableLPIndex()] != 0;
		if (result) {
			for (int i = 0; i < solution.length; i++) {
				if (solution[i] != 0) {
					if (i != varMap.getMarkingVariableLPIndex()) {
						result = false;
						break;
					}
				}
			}
		}
		return result;
	}

	private static <T extends HybridVariableMapping<Integer>, S> Petrinet connectPlaceToLPIndexBasedSolution(
			int lpIndex, int solutionValue, Petrinet pn, Place place, T varMap, LPLogAbstraction<S> logAbstraction,
			double includeThreshold) {
		S event = logAbstraction.decode(varMap.getObjectOfLpIndex(lpIndex));
		Transition transition = getTransitionByLabel(pn, event.toString());
		if (varMap.isSingleVariableIndex(lpIndex)) {
			if (solutionValue > 0 && solutionValue >= includeThreshold) {
				pn.addArc(transition, place, solutionValue);
			} else if (solutionValue < 0 && solutionValue <= -1 * includeThreshold) {
				pn.addArc(place, transition, -1 * solutionValue);
			}
		} else if (varMap.isXVariableIndex(lpIndex) && solutionValue >= includeThreshold) {
			pn.addArc(transition, place, solutionValue);
		} else if (varMap.isYVariableIndex(lpIndex) && solutionValue >= includeThreshold) {
			pn.addArc(place, transition, solutionValue);
		}
		return pn;

	}

	private static Transition getTransitionByLabel(Petrinet pn, String label) {
		Transition result = null;
		for (Transition t : pn.getTransitions()) {
			if (t.getLabel().equals(label)) {
				result = t;
				break;
			}
		}
		return result;
	}

	private static boolean isSolution(double[] solution) {
		boolean result = false;
		if (solution.length > 0) {
			for (int i = 0; i < solution.length; i++) {
				if (Math.round(solution[i]) != 0) {
					result = true;
					break;
				}
			}
		}
		return result;
	}

	public static <T extends HybridVariableMapping<XEventClass>> Pair<Petrinet, Marking> constructMarkedPetrinet(
			T varMap, Collection<double[]> solutions) {
		Petrinet net = PetrinetFactory.newPetrinet("Net (Created by ILP Based Process Discovery Result Converter)");
		Map<XEventClass, Transition> transitions = new HashMap<>();
		Marking marking = new Marking();
		for (XEventClass transition : varMap.getDomain()) {
			transitions.put(transition, net.addTransition(transition.getId()));
		}

		for (double[] solution : solutions) {
			if (isSolution(solution)) {
				Place p = net.addPlace("p_" + net.getPlaces().size());
				if (solution[varMap.getMarkingVariableLPIndex()] > 0) {
					marking.add(p, (int) Math.round(solution[varMap.getMarkingVariableLPIndex()]));
				}
				for (int i = 0; i < solution.length; i++) {
					if (varMap.isSingleVariableIndex(i)) {
						if (solution[i] < 0) {
							net.addArc(p,transitions.get(varMap.getObjectOfLpIndex(i)), (int) (-1 * Math.round(solution[i])));
						} else if (solution[i] > 0) {
							net.addArc(transitions.get(varMap.getObjectOfLpIndex(i)), p, (int) Math.round(solution[i]));
						}
					} else if (varMap.isXVariableIndex(i) && solution[i] > 0) {
							net.addArc(transitions.get(varMap.getObjectOfLpIndex(i)), p, (int) Math.round(solution[i]));
					} else if (varMap.isYVariableIndex(i) && solution[i] > 0) {
						net.addArc(p,transitions.get(varMap.getObjectOfLpIndex(i)),(int) Math.round(solution[i]));
					}
				}
			}
			
		}
		return new Pair<>(net,marking);
	}

	public static <T extends HybridVariableMapping<Integer>, S> Pair<Petrinet, Marking> translateSolutionsToMarkedNet(
			Set<double[]> solutions, Pair<Petrinet, Marking> markedNet, T varMap, LPLogAbstraction<S> logAbstraction) {
		for (double[] solution : solutions) {
			markedNet = LPSolutionUtilities.translateSolutionToPlace(solution, markedNet.getFirst(),
					markedNet.getSecond(), varMap, logAbstraction);
		}
		return markedNet;
	}

	public static <T extends HybridVariableMapping<Integer>, S> Pair<Petrinet, Marking> translateSolutionToPlace(
			double[] solution, Petrinet pn, Marking marking, T varMap, LPLogAbstraction<S> logAbstraction) {
		return translateSolutionToPlace(solution, pn, marking, varMap, logAbstraction, 1.0d);
	}

	public static <T extends HybridVariableMapping<Integer>, S> Pair<Petrinet, Marking> translateSolutionToPlace(
			double[] solution, Petrinet pn, Marking marking, T varMap, LPLogAbstraction<S> logAbstraction,
			double includeThreshold) {
		if (isSolution(solution)) {
			Place place = pn.addPlace("place_" + pn.getPlaces().size());
			for (int i = 1; i < solution.length; i++) {
				int value = (int) Math.round(solution[i]);
				if (value != 0) {
					if (varMap.isEventRelatedVariableIndex(i)) {
						pn = connectPlaceToLPIndexBasedSolution(i, value, pn, place, varMap, logAbstraction,
								includeThreshold);
					} else if (i == varMap.getMarkingVariableLPIndex()) {
						marking.add(place, value);
					}
				}
			}
		}
		return new Pair<>(pn, marking);
	}

	// private static Pair<Petrinet, Making> int addSourcePlaces(int counter) {
	// // if (this.sourcePlaces.size() > 1) {
	// // String sourceTau = "|invisible_start>";
	// // while (this.transitions.keySet().contains(sourceTau)) {
	// // Random r = new Random();
	// // sourceTau += r.nextInt(10) + 1;
	// // }
	// // Transition invisibleSourceTransition = this.petrinet
	// // .addTransition(sourceTau);
	// // invisibleSourceTransition.setInvisible(true);
	// // this.transitions.put(sourceTau, invisibleSourceTransition);
	// //
	// // // add artificial source place
	// // Set<String> sourceTauSetRepresentation = new HashSet<String>();
	// // sourceTauSetRepresentation.add(sourceTau);
	// // Pair<Set<String>, Set<String>> arcs = new Pair<Set<String>,
	// Set<String>>(
	// // new HashSet<String>(), sourceTauSetRepresentation);
	// // this.addPlace(new Pair<>(arcs, 1), "source");
	// //
	// // for (Pair<Set<String>, Integer> source : sourcePlaces) {
	// // arcs = new Pair<Set<String>, Set<String>>(
	// // sourceTauSetRepresentation, source.getFirst());
	// // addPlace(new Pair<>(arcs, 0), "p_" + counter);
	// // counter++;
	// // }
	// //
	// // } else if (sourcePlaces.size() == 1) {
	// // for (Pair<Set<String>, Integer> source : sourcePlaces) {
	// // Pair<Set<String>, Set<String>> arcs = new Pair<Set<String>,
	// Set<String>>(
	// // new HashSet<String>(), source.getFirst());
	// // addPlace(new Pair<>(arcs, source.getSecond()), "source");
	// // }
	// // }
	// // return counter;
	// //}

}
