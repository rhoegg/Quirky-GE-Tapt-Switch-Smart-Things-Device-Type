/**
  *  Custom Device type for Quirky GE Tapt
  *
  *  Initial Release 1/11/2015
  *
  *  Copyright 2014 Matt Frank using code from JohnR / John Rucker's Dual Relay Controller
  *
  *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
  *  in compliance with the License. You may obtain a copy of the License at:
  *
  *      http://www.apache.org/licenses/LICENSE-2.0
  *
  *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
  *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
  *  for the specific language governing permissions and limitations under the License.
  *
  */
 metadata {
   definition (name: "GE Tapt", namespace: "Matt Frank", author: "Matt Frank") {
         capability "Refresh"
         capability "Polling"
         capability "Sensor"
         capability "Configuration"
         capability "Switch"




       //fingerprint profileId: "0104", inClusters: "0000", outClusters: "000D,0006"
         fingerprint inClusters: "0000 0001 0003 0004 0005 0006", endpointId: "01", deviceId: "0100", profileId: "0104"

   }

   // simulator metadata
   simulator {
     }

   // UI tile definitions
   tiles {



     standardTile("switch", "device.switch2", width: 2, height: 2, canChangeIcon: true) {
       state "off", label: '${name}', action: "Switch.on", icon: "st.switches.switch.off", backgroundColor: "#ffffff"
       state "on", label: '${name}', action: "Switch.off", icon: "st.switches.switch.on", backgroundColor: "#79b821"
     }

         standardTile("refresh", "device.refresh", inactiveLabel: false, decoration: "flat") {
       state "default", action:"refresh.refresh", icon:"st.secondary.refresh"
     }

     main (["switch"])
     details (["switch", "refresh"])
   }
 }

 // Parse incoming device messages to generate events
 def parse(String description) {
     log.debug "Parse description $description"
     def name = null
     def value = null

      if (description?.startsWith("catchall: 0104 0006 02")) {
         log.debug "On/Off command received"
         if (description?.endsWith(" 01 0140 00 38A8 00 00 0000 01 01 0000001000")){
           name = "switch"
             value = "off"}
         else if (description?.endsWith(" 01 0140 00 38A8 00 00 0000 01 01 0000001001")){
           name = "switch"
             value = "on"}
     }

   def result = createEvent(name: name, value: value)
     log.debug "Parse returned ${result?.descriptionText}"
     return result
 }

 // Commands to device


 def on() {
   log.debug "Switch on()"
   sendEvent(name: "switch", value: "on")
   "st cmd 0x${device.deviceNetworkId} 0x02 0x0006 0x1 {}"
 }

 def off() {
   log.debug "Switch off()"
   sendEvent(name: "switch", value: "off")
   "st cmd 0x${device.deviceNetworkId} 0x02 0x0006 0x0 {}"
 }

 def poll(){
   log.debug "Poll is calling refresh"
   refresh()
 }

 def refresh() {
   log.debug "sending refresh command"
     def cmd = []



     cmd << "st rattr 0x${device.deviceNetworkId} 0x01 0x0006 0x0000"	//  on / off value

     cmd
 }



 def configure() {
   log.debug "Binding SEP  0x02 "
     def cmd = []
     cmd << "delay 150"
     cmd << "zdo bind 0x${device.deviceNetworkId} 0x02 0x01 0x0006 {${device.zigbeeId}} {}"
     cmd
 }
