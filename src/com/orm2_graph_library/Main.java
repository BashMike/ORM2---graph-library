package com.orm2_graph_library;

import com.orm2_graph_library.core.Diagram;
import com.orm2_graph_library.nodes.common.DataType;
import com.orm2_graph_library.nodes.common.EntityType;
import com.orm2_graph_library.nodes.common.data_types.IntegerDataType;
import com.orm2_graph_library.nodes.predicates.ObjectifiedPredicate;
import com.orm2_graph_library.nodes.predicates.Predicate;
import com.thoughtworks.xstream.XStream;

public class Main {
    public static void main(String[] args) {
        Diagram diagram = new Diagram();
        diagram.addNode(new EntityType());
        diagram.addNode(new ObjectifiedPredicate(new Predicate(4)));

        diagram.saveToXmlFile("Hello.xml", false);
        diagram = Diagram.loadFromXmlFile("Hello.xml");
        System.out.println("HELLO");
    }
}
