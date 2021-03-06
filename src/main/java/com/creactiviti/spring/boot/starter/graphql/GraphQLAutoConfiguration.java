package com.creactiviti.spring.boot.starter.graphql;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import graphql.GraphQL;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLObjectType.Builder;
import graphql.schema.GraphQLSchema;
import graphql.schema.GraphQLType;

/**
 * @author Arik Cohen
 * @since Feb 07, 2018
 */
@Configuration
@ComponentScan(basePackages="com.creactiviti.spring.boot.starter.graphql")
public class GraphQLAutoConfiguration  {
  
  @Autowired(required=false)
  private List<QueryBuilder> queryBuilders = new ArrayList<>();
  
  @Autowired(required=false)
  private List<MutationBuilder> mutationBuilders = new ArrayList<>();
  
  @Autowired(required=false)
  private List<SubscriptionBuilder> subscriptionBuilders = new ArrayList<>();
  
  @Autowired(required=false)
  private List<TypeBuilder> typeBuilders = new ArrayList<>();
  
  @Bean
  public GraphQL graphql () {
    // build the Query (Read) portion of the GraphQL schema
    Builder queryBuilder = GraphQLObjectType.newObject().name("Query");
    
    queryBuilders.forEach(qb->qb.build(queryBuilder));
    
    // build the Mutation (Write) portion of the GraphQL schema    
    
    Builder mutationBuilder = GraphQLObjectType.newObject().name("Mutation");
    
    mutationBuilders.forEach(mb->mb.build(mutationBuilder));
    
    Builder subscriptionBuilder = GraphQLObjectType.newObject().name("Subscription");
    
    subscriptionBuilders.forEach(sb->sb.build(subscriptionBuilder));
    
    // build all types
    
    List<GraphQLType> types = typeBuilders.stream().map(tb->tb.build()).collect(Collectors.toList());
    
    // build the GraphQL schema
    
    GraphQLSchema.Builder schemaBuilder = GraphQLSchema.newSchema();
    
    if(queryBuilders.size() > 0) {
      schemaBuilder.query(queryBuilder);
    }
    
    if(mutationBuilders.size() > 0) {
      schemaBuilder.mutation(mutationBuilder);
    }
    
    if(subscriptionBuilders.size() > 0) {
      schemaBuilder.subscription(subscriptionBuilder);
    }
    
    return GraphQL.newGraphQL(schemaBuilder.additionalTypes(new HashSet<>(types)).build()).build();
  }
  
}
