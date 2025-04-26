package com.multi.tenants.api.repository;

import com.multi.tenants.api.model.Group;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface GroupRepository extends JpaRepository<Group, Long>, JpaSpecificationExecutor<Group> {
   Group findFirstByName(String name);

   @Query("SELECT g  FROM Group g where g.kind = :kind")
   Page<Group> findAllByKind(@Param("kind") int kind, Pageable pageable);
}
