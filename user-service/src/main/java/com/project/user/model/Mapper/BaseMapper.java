package com.project.user.model.Mapper;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.List;

public abstract class BaseMapper<E, D> {
    public abstract E ConvertToEntity(D dto, Object... args);

    public abstract D ConvertToDto(E entity, Object... args);

    public Collection<E> ConvertToEntity(Collection<D> dto, Object... args){
        return dto.stream().map(d -> ConvertToEntity(d, args)).collect(Collectors.toList());
    }

    public Collection<D> ConvertToDto(Collection<E> entity, Object... args){
        return entity.stream().map(e -> ConvertToDto(e, args)).collect(Collectors.toList());
    }

    public List<E> ConvertToEntity(List<D> dto, Object... args){
        return ConvertToEntity(dto, args).stream().collect(Collectors.toList());
    }

    public List<D> ConvertToDto(List<E> entity, Object... args){
        return ConvertToDto(entity, args).stream().collect(Collectors.toList());
    }

    public Set<E> ConvertToEntity(Set<D> dto, Object... args){
        return ConvertToEntity(dto, args).stream().collect(Collectors.toSet());
    }

    public Set<D> ConvertToDto(Set<E> entity, Object... args){
        return ConvertToDto(entity, args).stream().collect(Collectors.toSet());
    }
}
