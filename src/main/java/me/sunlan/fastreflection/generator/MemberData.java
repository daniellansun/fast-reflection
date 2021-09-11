/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package me.sunlan.fastreflection.generator;

import java.lang.reflect.Member;

public class MemberData {
    private final Member member;
    private final String name;
    private final byte[] bytes;

    public MemberData(Member member, String name, byte[] bytes) {
        this.member = member;
        this.name = name;
        this.bytes = bytes;
    }

    public Member getMember() {
        return member;
    }
    public String getName() {
        return name;
    }
    public byte[] getBytes() {
        return bytes;
    }
}
