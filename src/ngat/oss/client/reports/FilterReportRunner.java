/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ngat.oss.client.reports;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import ngat.oss.client.AccessModelClient;
import ngat.oss.client.AccountModelClient;
import ngat.oss.client.Phase2ModelClient;
import ngat.oss.exception.Phase2Exception;
import ngat.oss.reference.Const;
import ngat.phase2.IAccount;
import ngat.phase2.IExecutiveAction;
import ngat.phase2.IGroup;
import ngat.phase2.IInstrumentConfig;
import ngat.phase2.IInstrumentConfigSelector;
import ngat.phase2.IProposal;
import ngat.phase2.ISemester;
import ngat.phase2.ISemesterPeriod;
import ngat.phase2.ISequenceComponent;
import ngat.phase2.XExecutiveComponent;
import ngat.phase2.XFilterDef;
import ngat.phase2.XFilterSpec;
import ngat.phase2.XImagerInstrumentConfig;
import ngat.phase2.XIteratorComponent;

/**
 *
 * @author nrc
 */
public class FilterReportRunner {
    
    //used to map filter name against the count of the number of instances of that filter
    private static Map<String, Integer> filterCountMap = new HashMap<String, Integer>();

    private static int debugTry = 15;
    
    //Prints out to terminal the list of filters and their incidence in sequences in that particular semester
    public static void runFilterQuery() throws Phase2Exception {
        
        Phase2ModelClient p2client = Phase2ModelClient.getInstance();
        AccessModelClient accessModelClient = AccessModelClient.getInstance();
       
        AccountModelClient proposalAccountClient = new AccountModelClient(Const.PROPOSAL_ACCOUNT_SERVICE);
        
        ISemesterPeriod semPeriod = proposalAccountClient.getSemesterPeriodOfDate(new Date().getTime());
        ISemester firstSemester = semPeriod.getFirstSemester();
        //ISemester secondSemester = semPeriod.getSecondSemester();
        
        System.err.println(firstSemester);
        //just going to deal with first semester.
        
        List accounts = proposalAccountClient.listAccountsOfSemester(firstSemester.getID());
        Iterator ai = accounts.iterator();
        
        while (ai.hasNext()) {
            IAccount account = (IAccount) ai.next();
            long proposalId = proposalAccountClient.getAccountOwnerID(account.getID());
            IProposal proposal = p2client.getProposal(proposalId);
            //System.out.println(proposal);
            
            List groups = p2client.listGroups(proposalId, true);
            Iterator gi = groups.iterator();
            while (gi.hasNext()) {
                IGroup group = (IGroup)gi.next();
                ISequenceComponent seqComp = p2client.getObservationSequenceOfGroup(group.getID());
                //System.err.println(seqComp);
                if (seqComp instanceof XIteratorComponent) {
                    XIteratorComponent iteratorComponent = (XIteratorComponent) seqComp;
                    
                    List childComponents = iteratorComponent.listChildComponents();
                    Iterator ci = childComponents.iterator();
                    while (ci.hasNext()) {
                        ISequenceComponent iSequenceComponent = (ISequenceComponent) ci.next();
                        if (iSequenceComponent instanceof XExecutiveComponent) {
                            XExecutiveComponent executiveComponent = (XExecutiveComponent) iSequenceComponent;
                            IExecutiveAction executiveAction = executiveComponent.getExecutiveAction();
                            
                            if (executiveAction instanceof IInstrumentConfigSelector) {
                                IInstrumentConfigSelector configSelector = (IInstrumentConfigSelector) executiveAction;
                                IInstrumentConfig instrumentConfig = configSelector.getInstrumentConfig();
                                
                                if (instrumentConfig instanceof XImagerInstrumentConfig) {
                                    XImagerInstrumentConfig imagerInstrumentConfig = (XImagerInstrumentConfig) instrumentConfig;
                                    if (imagerInstrumentConfig.getInstrumentName().equalsIgnoreCase("IO:O")) {
                                        
                                        XFilterSpec filterSpec= imagerInstrumentConfig.getFilterSpec();
                                        
                                        List filterList = filterSpec.getFilterList();
                                        
                                        Iterator fi = filterList.iterator();
                                        while(fi.hasNext()) {
                                            XFilterDef filterDef = (XFilterDef) fi.next();
                                            receiveFoundFilter(filterDef);
                                        }                                        
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        //finished loops
        showFilterCountMap();
    }
    
    
    //takes a filter and stores a count of it's receipt in Map
    private static void receiveFoundFilter(XFilterDef filterDef) {
        
        /********************************/
        /************ DEBUG *************/
        /********************************/
        /*
        debugTry--;
        if (debugTry < 0) {
            showFilterCountMap();
            System.exit(0);
        } else {
            System.err.println("debugTry=" + debugTry);
        }
        */
        /********************************/
        String filterName = filterDef.getFilterName();
        System.err.println("receiveFoundFilter(" + filterName + ")");
        
        //first, does the filterName exist as a key in filterCountMap?
        if (filterCountMap.containsKey(filterName)) {
            //it does, take the key value, increment it and put it back in the map
            int filterCount = filterCountMap.get(filterName);
            filterCount++;
            filterCountMap.remove(filterName);
            filterCountMap.put(filterName, filterCount);
        } else {
            //create a new entry for this filter in the map, and put 1 into the value field
            filterCountMap.put(filterName, 1);
        }
    }
    
    private static void showFilterCountMap() {
        Set keys = filterCountMap.keySet();
        Iterator ki = keys.iterator();
        while (ki.hasNext()) {
            String filterName = (String) ki.next();
            Integer filterCountI = filterCountMap.get(filterName);
            System.out.println("[" + filterName + "," + filterCountI + "]");
        }
    }
}
