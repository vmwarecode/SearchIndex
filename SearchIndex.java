/*
 * ****************************************************************************
 * Copyright VMware, Inc. 2010-2016.  All Rights Reserved.
 * ****************************************************************************
 *
 * This software is made available for use under the terms of the BSD
 * 3-Clause license:
 *
 * Redistribution and use in source and binary forms, with or without 
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright 
 *    notice, this list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in 
 *    the documentation and/or other materials provided with the 
 *    distribution.
 * 
 * 3. Neither the name of the copyright holder nor the names of its 
 *    contributors may be used to endorse or promote products derived
 *    from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
 * FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE 
 * COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS
 * OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR
 * TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE
 * USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */


package com.vmware.general;

import com.vmware.common.annotations.Action;
import com.vmware.common.annotations.Option;
import com.vmware.common.annotations.Sample;
import com.vmware.connection.ConnectedVimServiceBase;
import com.vmware.vim25.InvalidPropertyFaultMsg;
import com.vmware.vim25.ManagedObjectReference;
import com.vmware.vim25.RuntimeFaultFaultMsg;

import javax.xml.ws.soap.SOAPFaultException;
import java.util.Map;

/**
 * <pre>
 * SearchIndex
 *
 * This sample demonstrates the SearchIndex API
 *
 * <b>Parameters:</b>
 * url          [required] : url of the web service
 * username     [required] : username for the authentication
 * password     [required] : password for the authentication
 * dcname       [required] : name of the datacenter
 * vmdnsname    [optional] : Dns of a virtual machine
 * hostdnsname  [optional] : Dns of the ESX host
 * vmpath       [optional] : Inventory path of a virtual machine
 * vmip         [optional] : IP Address of a virtual machine
 *
 * <b>Command Line:</b>
 * Run the search index with dcName myDatacenter
 * run.bat com.vmware.general.SearchIndex --url [webserviceurl]
 * --username [username] --password [password] --dcName myDatacenter
 *
 * Run the search index with dcName myDatacenter and vmpath to virtual machine named Test
 * run.bat com.vmware.general.SearchIndex --url [webserviceurl]
 * --username [username] --password [password] --dcName myDatacenter
 * --vmpath //DatacenterName//vm//Test
 *
 * Run the search index with dcName myDatacenter and hostdns 'abc.bcd.com'
 * run.bat com.vmware.general.SearchIndex --url [webserviceurl]
 * --username [username] --password [password]
 * --dcName myDatacenter --hostDns abc.bcd.com
 *
 * Run the search index with dcName myDatacenter and ip of the vm as 111.123.155.21
 * run.bat com.vmware.general.SearchIndex --url [webserviceurl]
 * --username [username] --password [password]
 * --dcName myDatacenter --vmIP 111.123.155.21
 * </pre>
 */
@Sample(name = "search-index", description = "This sample demonstrates the SearchIndex API")
public class SearchIndex extends ConnectedVimServiceBase {
    public final String SVC_INST_NAME = "ServiceInstance";
    public final String PROP_ME_NAME = "name";

    private String dcName;
    private String vmDnsName;
    private String vmPath;
    private String hostDnsName;
    private String vmIP;

    @Option(name = "dcname", description = "name of the datacenter")
    public void setDcName(String dcName) {
        this.dcName = dcName;
    }

    @Option(name = "vmdnsname", required = false, description = "Dns of a virtual machine")
    public void setVmDnsName(String vmDnsName) {
        this.vmDnsName = vmDnsName;
    }

    @Option(name = "hostdnsname", required = false, description = "Dns of the ESX host")
    public void setHostDnsName(String hostDnsName) {
        this.hostDnsName = hostDnsName;
    }

    @Option(name = "vmpath", required = false, description = "Inventory path of a virtual machine")
    public void setVmPath(String vmPath) {
        this.vmPath = vmPath;
    }

    @Option(name = "vmip", required = false, description = "IP Address of a virtual machine")
    public void setVmIP(String vmIP) {
        this.vmIP = vmIP;
    }

    public void printSoapFaultException(SOAPFaultException sfe) {
        System.out.println("SOAP Fault -");
        if (sfe.getFault().hasDetail()) {
            System.out.println(sfe.getFault().getDetail().getFirstChild()
                    .getLocalName());
        }
        if (sfe.getFault().getFaultString() != null) {
            System.out.println("\n Message: " + sfe.getFault().getFaultString());
        }
    }

    @Action
    public void action() throws RuntimeFaultFaultMsg, InvalidPropertyFaultMsg {
        Map<String, ManagedObjectReference> entities =
                getMOREFs.inFolderByType(serviceContent.getRootFolder(),
                        "Datacenter");
        ManagedObjectReference dcMoRef = entities.get(dcName);

        if (dcMoRef != null) {
            System.out.println("Found Datacenter with name: " + dcName
                    + ", MoRef: " + dcMoRef.getValue());
        } else {
            System.out.println("Datacenter not Found with name: " + dcName);
            return;
        }

        if (vmDnsName != null) {

            ManagedObjectReference vmMoRef = null;
            try {
                vmMoRef =
                        vimPort.findByDnsName(serviceContent.getSearchIndex(),
                                dcMoRef, vmDnsName, true);
            } catch (SOAPFaultException sfe) {
                printSoapFaultException(sfe);
            } catch (RuntimeFaultFaultMsg ex) {
                System.out.println("Error Encountered: " + ex);
            }
            if (vmMoRef != null) {
                System.out.println("Found VirtualMachine with DNS name: "
                        + vmDnsName + ", MoRef: " + vmMoRef.getValue());
            } else {
                System.out.println("VirtualMachine not Found with DNS name: "
                        + vmDnsName);
            }
        }
        if (vmPath != null) {
            ManagedObjectReference vmMoRef = null;
            try {
                vmMoRef =
                        vimPort.findByInventoryPath(
                                serviceContent.getSearchIndex(), vmPath);
            } catch (SOAPFaultException sfe) {
                printSoapFaultException(sfe);
            } catch (RuntimeFaultFaultMsg ex) {
                System.out.println("Error Encountered: " + ex);
            }
            if (vmMoRef != null) {
                System.out.println("Found VirtualMachine with Path: " + vmPath
                        + ", MoRef: " + vmMoRef.getValue());

            } else {
                System.out.println("VirtualMachine not found with vmPath "
                        + "address: " + vmPath);
            }
        }
        if (vmIP != null) {
            ManagedObjectReference vmMoRef = null;
            try {
                vmMoRef =
                        vimPort.findByIp(serviceContent.getSearchIndex(), dcMoRef,
                                vmIP, true);
            } catch (SOAPFaultException sfe) {
                printSoapFaultException(sfe);
            } catch (RuntimeFaultFaultMsg ex) {
                System.out.println("Error Encountered: " + ex);
            }
            if (vmMoRef != null) {
                System.out.println("Found VirtualMachine with IP " + "address "
                        + vmIP + ", MoRef: " + vmMoRef.getValue());
            } else {
                System.out.println("VirtualMachine not found with IP "
                        + "address: " + vmIP);
            }
        }
        if (hostDnsName != null) {
            ManagedObjectReference hostMoRef = null;
            try {
                hostMoRef =
                        vimPort.findByDnsName(serviceContent.getSearchIndex(),
                                null, hostDnsName, false);
            } catch (SOAPFaultException sfe) {
                printSoapFaultException(sfe);
            } catch (RuntimeFaultFaultMsg ex) {
                System.out.println("Error Encountered: " + ex);
            }
            if (hostMoRef != null) {
                System.out.println("Found HostSystem with DNS name "
                        + hostDnsName + ", MoRef: " + hostMoRef.getValue());
            } else {
                System.out.println("HostSystem not Found with DNS name:"
                        + hostDnsName);
            }
        }
    }
}
