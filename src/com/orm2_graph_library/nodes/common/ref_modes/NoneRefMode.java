package com.orm2_graph_library.nodes.common.ref_modes;

import com.orm2_graph_library.nodes.common.RefMode;
import org.jetbrains.annotations.NotNull;

public class NoneRefMode extends RefMode {
    @Override
    public boolean equals(@NotNull Object other) { return (other instanceof NoneRefMode); }
}
