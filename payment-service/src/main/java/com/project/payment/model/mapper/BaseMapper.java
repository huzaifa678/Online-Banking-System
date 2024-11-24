package com.project.payment.model.mapper;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class BaseMapper<Doc, D> {
    public abstract Doc ConvertToDocument(D dto, Object... args);

    public abstract D ConvertToDto(Doc document, Object... args);

    public Collection<Doc> ConvertToDocument(Collection<D> dto, Object... args){
        return dto.stream().map(d -> ConvertToDocument(d, args)).collect(Collectors.toList());
    }

    public Collection<D> ConvertToDto(Collection<Doc> document, Object... args){
        return document.stream().map(doc -> ConvertToDto(doc, args)).collect(Collectors.toList());
    }

    public List<Doc> ConvertToDocument(List<D> dto, Object... args){
        return ConvertToDocument(dto, args).stream().collect(Collectors.toList());
    }

    public List<D> ConvertToDto(List<Doc> document, Object... args){
        return ConvertToDto(document, args).stream().collect(Collectors.toList());
    }

    public Set<Doc> ConvertToDocument(Set<D> dto, Object... args){
        return ConvertToDocument(dto, args).stream().collect(Collectors.toSet());
    }

    public Set<D> ConvertToDto(Set<Doc> document, Object... args){
        return ConvertToDto(document, args).stream().collect(Collectors.toSet());
    }
}

