/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ngat.oss.client.gui.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import ngat.oss.client.gui.reference.CONST;
import ngat.phase2.IAcquisitionConfig;
import ngat.phase2.IExecutiveAction;
import ngat.phase2.IExposure;
import ngat.phase2.IInstrumentConfig;
import ngat.phase2.IInstrumentConfigSelector;
import ngat.phase2.ISequenceComponent;
import ngat.phase2.ISlew;
import ngat.phase2.XBranchComponent;
import ngat.phase2.XDark;
import ngat.phase2.XExecutiveComponent;
import ngat.phase2.XInstrumentConfigSelector;
import ngat.phase2.XIteratorComponent;
import ngat.phase2.XMultipleExposure;
import org.apache.log4j.Logger;

/**
 *
 * @author nrc
 */
public class SequenceWalker {
    static Logger logger = Logger.getLogger(SequenceWalker.class);

    private ISequenceComponent rootComponent;
    private List allChildrenList = new ArrayList();

    public SequenceWalker(ISequenceComponent rootComponent) {
        this.rootComponent = rootComponent;
        addAllChildrenInSequenceToList();
    }

    public List getListOfAllChildren() {
        return allChildrenList;
    }

    private List addAllChildrenInSequenceToList() {
        if (rootComponent != null) {
            List childrenList = rootComponent.listChildComponents();
            if (childrenList != null) {
                Iterator childrenIterator = childrenList.iterator();
                while (childrenIterator.hasNext()) {
                    ISequenceComponent childComponent = (ISequenceComponent) childrenIterator.next();
                    addChildrenOfComponentToList(childComponent);
                }
            }
        }
        return allChildrenList;
    }

    private void addChildrenOfComponentToList(ISequenceComponent component) {
        allChildrenList.add(component);
        if ((component instanceof XIteratorComponent) | (component instanceof XBranchComponent)) {
            List childrenList = component.listChildComponents();
            if (childrenList != null) {
                Iterator childrenIterator = childrenList.iterator();
                while (childrenIterator.hasNext()) {
                    ISequenceComponent childComponent = (ISequenceComponent) childrenIterator.next();
                    addChildrenOfComponentToList(childComponent);
                }
            }
        }
    }

    //returns whether the sequence can be considered to be a LOTUS sequence 
    //(i.e. it either has a Instrument config that selects LOTUS or a fine tune that uses LOTUS as the science instrument).
    public boolean isALOTUSSequence() {
        if (allChildrenList != null) {
            Iterator childrenIterator = allChildrenList.iterator();
            while (childrenIterator.hasNext()) {
                ISequenceComponent childComponent = (ISequenceComponent) childrenIterator.next();
                if (childComponent instanceof XExecutiveComponent) {
                    XExecutiveComponent childExecComponent = (XExecutiveComponent) childComponent;
                    IExecutiveAction executiveAction = childExecComponent.getExecutiveAction();
                    if (executiveAction instanceof IAcquisitionConfig) {
                        IAcquisitionConfig acquisitionConfig = (IAcquisitionConfig) executiveAction;
                        String scienceInstrument = acquisitionConfig.getTargetInstrumentName();
                        if (scienceInstrument != null) {
                            if (scienceInstrument.equals(CONST.LOTUS)) {
                                return true;
                            }
                        }
                    } else if (executiveAction instanceof IInstrumentConfigSelector) {
                        IInstrumentConfigSelector instrumentConfigSelector = (IInstrumentConfigSelector) executiveAction;
                        IInstrumentConfig instrumentConfig = instrumentConfigSelector.getInstrumentConfig();
                        if (instrumentConfig != null) {
                            String instrumentName = instrumentConfig.getInstrumentName();
                            if (instrumentName != null) {
                                if (instrumentName.equals(CONST.LOTUS)) {
                                    return true;
                                }
                            }
                        }
                    }
                }
            }
        }
        return false;
    }
    
    public boolean sequenceContainsFrodoBranch() {

        if (allChildrenList != null) {
            Iterator childrenIterator = allChildrenList.iterator();
            while (childrenIterator.hasNext()) {
                ISequenceComponent childComponent = (ISequenceComponent) childrenIterator.next();
                if (childComponent instanceof XBranchComponent) {
                    if (childComponent.getComponentName().equalsIgnoreCase(CONST.FRODO)){
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public List getExposures() {
        ArrayList exposuresList = new ArrayList();

        if (allChildrenList != null) {
            Iterator childrenIterator = allChildrenList.iterator();
            while (childrenIterator.hasNext()) {
                ISequenceComponent childComponent = (ISequenceComponent) childrenIterator.next();
                if (childComponent instanceof XExecutiveComponent) {
                    XExecutiveComponent executiveComponent = (XExecutiveComponent) childComponent;
                    IExecutiveAction executiveAction = executiveComponent.getExecutiveAction();
                    if (executiveAction instanceof IExposure) {
                        exposuresList.add((IExposure)executiveAction);
                    }
                }
            }
        }
        return exposuresList;
    }

    public List getSlews() {
        ArrayList slewsList = new ArrayList();
        
        if (allChildrenList != null) {
            Iterator childrenIterator = allChildrenList.iterator();
            while (childrenIterator.hasNext()) {
                ISequenceComponent childComponent = (ISequenceComponent) childrenIterator.next();
                if (childComponent instanceof XExecutiveComponent) {
                    XExecutiveComponent executiveComponent = (XExecutiveComponent) childComponent;
                    IExecutiveAction executiveAction = executiveComponent.getExecutiveAction();
                    if (executiveAction instanceof ISlew) {
                        slewsList.add((ISlew)executiveAction);
                    }
                }
            }
        }
        return slewsList;
    }


    /**
     * look for parts of the sequence that are using supircam, load them into supircamSequence
     * find exposures in that sequence
     * make sure they are surrounded by darks, otherwise return true
     * @return false, if all is ok
     */
    public boolean sequenceContainsInvalidSupircamExposure() {

        //look for parts of the sequence that are using supircam, load them into supircamSequence
        //find exposures in that sequence
        //make sure they are surrounded by darks, otherwise return true
        //return false if all is ok

        //get supircam only sequence parts
        ArrayList supircamSequence = getSupIRCamOnlySequence();

        //System.err.println("supircamSequence.size()=" + supircamSequence.size());
        //System.err.println("supircamSequence=" + supircamSequence);

        //get index list of exposure components in supircamSequence
        ArrayList exposureLocations = getExposureIndexes(supircamSequence);

        //System.err.println("exposureLocations.size()=" + exposureLocations.size());
        //System.err.println("exposureLocations=" + exposureLocations);

        Iterator eli = exposureLocations.iterator();
        while (eli.hasNext()) {
            //get index of exposure
            int indexOfExposure = ((Integer)eli.next()).intValue();

            ISequenceComponent componentBefore, componentAfter;
            XMultipleExposure componentAsMultipleExposure;

            try {
                componentBefore = (ISequenceComponent) supircamSequence.get(indexOfExposure - 1);
                componentAsMultipleExposure = getExposureFromSequenceComponent((ISequenceComponent) supircamSequence.get(indexOfExposure));
                componentAfter = (ISequenceComponent) supircamSequence.get(indexOfExposure + 1);
            } catch (IndexOutOfBoundsException ioobe) {
                return true;
            }

            if (componentBefore == null) {
                return true;
            }

            if (componentAfter == null) {
                return true;
            }

            XDark componentBeforeAsDark, componentAfterAsDark;
            try {
                componentBeforeAsDark = getDark(componentBefore);
                componentAfterAsDark = getDark(componentAfter);
            } catch(ClassCastException cce) {
                return true;
            }

            double exposureTime = componentAsMultipleExposure.getExposureTime();
            if (componentBeforeAsDark.getExposureTime() != exposureTime) {
                return true;
            }
            if (componentAfterAsDark.getExposureTime() != exposureTime) {
                return true;
            }
        }

        return false;
    }

    //may throw class cast exceptions if things go wrong
    private XMultipleExposure getExposureFromSequenceComponent(ISequenceComponent sequenceComponent) {
        XExecutiveComponent executiveComponent = (XExecutiveComponent) sequenceComponent;
        IExecutiveAction executiveAction = executiveComponent.getExecutiveAction();
        //the exposure should be a XMultipleExposure
        XMultipleExposure exposure = (XMultipleExposure) executiveAction;
        return exposure;
    }

    //pull the XDark action out of the sequenceComponent
    //if one not there, return null
    private XDark getDark(ISequenceComponent sequenceComponent) {
        if (sequenceComponent == null) {
            return null;
        }

        if (sequenceComponent instanceof XExecutiveComponent) {
            XExecutiveComponent executiveComponent = (XExecutiveComponent) sequenceComponent;
            IExecutiveAction executiveAction = executiveComponent.getExecutiveAction();
            if (executiveAction instanceof XDark) {
                XDark dark = (XDark) executiveAction;
                return dark;
            }
        }
        return null;
    }

    private ArrayList getExposureIndexes(ArrayList supircamSequence) {
        ArrayList locationsOfExposures = new ArrayList();

        Iterator childrenIterator = supircamSequence.iterator();
        int p = 0;
        while (childrenIterator.hasNext()) {

            ISequenceComponent childComponent = (ISequenceComponent) childrenIterator.next();
            if (childComponent instanceof XExecutiveComponent) {
                XExecutiveComponent executiveComponent = (XExecutiveComponent) childComponent;
                IExecutiveAction  executiveAction = executiveComponent.getExecutiveAction();
                if (executiveAction instanceof XMultipleExposure) {
                    locationsOfExposures.add(p);
                }
            }

            p++;
        }
        return locationsOfExposures;
    }

    //pull out the sequence components that are in sequence blocks using supricam
    private ArrayList getSupIRCamOnlySequence() {
        ArrayList supircamSequence = new ArrayList();
        //boolean currentInstrumentIsSupircam = false;
        if (allChildrenList != null) {
            Iterator childrenIterator = allChildrenList.iterator();
            while (childrenIterator.hasNext()) {

                ISequenceComponent childComponent = (ISequenceComponent) childrenIterator.next();
                if (childComponent instanceof XExecutiveComponent) {
                    XExecutiveComponent executiveComponent = (XExecutiveComponent) childComponent;
                    IExecutiveAction  executiveAction = executiveComponent.getExecutiveAction();
                    if (executiveAction instanceof XInstrumentConfigSelector) {
                        XInstrumentConfigSelector instrumentConfigSelector = (XInstrumentConfigSelector) executiveAction;
                        String instrumentName = instrumentConfigSelector.getInstrumentConfig().getInstrumentName();
                        /*
                        if (instrumentName.equals(CONST.SUPIRCAM)) {
                            currentInstrumentIsSupircam = true;
                        } else {
                            currentInstrumentIsSupircam = false;
                        }
                        */
                    }
                }

                /*
                if (currentInstrumentIsSupircam) {
                    supircamSequence.add(childComponent);
                }
                */
            }
        }

        return supircamSequence;
    }

    private void debugShowChildList() {
        System.err.println("debugShowChildList()");
        if (allChildrenList != null) {
            Iterator childrenIterator = allChildrenList.iterator();
            while (childrenIterator.hasNext()) {
                ISequenceComponent childComponent = (ISequenceComponent) childrenIterator.next();
                System.err.println("... " + childComponent);
            }
        }
    }

    
}
